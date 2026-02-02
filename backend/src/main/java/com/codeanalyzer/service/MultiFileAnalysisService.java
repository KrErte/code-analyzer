package com.codeanalyzer.service;

import com.codeanalyzer.config.ClaudeConfig;
import com.codeanalyzer.dto.*;
import com.codeanalyzer.model.Persona;
import com.codeanalyzer.model.Severity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class MultiFileAnalysisService {

    private final WebClient claudeWebClient;
    private final ClaudeConfig claudeConfig;
    private final ObjectMapper objectMapper;

    private static final String MULTI_FILE_SYSTEM_PROMPT = """
        You are a senior software architect with 20+ years of experience reviewing codebases.
        You're analyzing a multi-file project submitted by a junior developer.

        Your job is to:
        1. Analyze EACH file for bugs, logic errors, and issues
        2. Find CROSS-FILE issues (inconsistencies, coupling problems, missing dependencies)
        3. Review the overall ARCHITECTURE (patterns, structure, maintainability)

        Be thorough and critical. Point to exact files and lines. Explain WHY things are wrong.

        IMPORTANT: Respond in valid JSON format with this EXACT structure:
        {
            "overallScore": <number 0-100, higher = more bugs/issues>,
            "summary": "<2-3 sentence overall assessment>",
            "fileFindings": [
                {
                    "filename": "<filename>",
                    "fileScore": <number 0-100>,
                    "findings": [
                        {
                            "severity": "<critical|warning|suggestion>",
                            "line": <line number or null>,
                            "issue": "<brief title>",
                            "explanation": "<detailed explanation>",
                            "suggestion": "<how to fix>"
                        }
                    ]
                }
            ],
            "crossFileIssues": [
                {
                    "issue": "<issue title>",
                    "explanation": "<why this is a problem>",
                    "affectedFiles": ["file1.java", "file2.java"],
                    "suggestion": "<how to fix>"
                }
            ],
            "architectureReview": {
                "overview": "<overall architecture description>",
                "strengths": ["<strength 1>", "<strength 2>"],
                "concerns": ["<concern 1>", "<concern 2>"],
                "recommendations": ["<recommendation 1>", "<recommendation 2>"]
            }
        }
        """;

    public Mono<MultiFileAnalysisResponse> analyzeMultipleFiles(MultiFileAnalysisRequest request) {
        String persona = request.getPersona() != null ? request.getPersona() : "brutal";
        String systemPrompt = buildSystemPrompt(persona);
        String userPrompt = buildUserPrompt(request);

        Map<String, Object> requestBody = buildRequest(systemPrompt, userPrompt);

        log.info("Analyzing {} files with persona: {}", request.getFiles().size(), persona);

        return claudeWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(response -> parseResponse(response, request.getFiles().size()))
                .doOnError(e -> log.error("Error in multi-file analysis: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(createErrorResponse(e.getMessage())));
    }

    private String buildSystemPrompt(String persona) {
        String personaModifier = switch (persona.toLowerCase()) {
            case "brutal" -> "\nBe BRUTAL. No mercy. Find every possible flaw. Imagine each bug costs $100,000.";
            case "mentor" -> "\nBe educational. Explain the 'why' behind issues. Help them learn.";
            case "edge-hunter" -> "\nFocus on edge cases, race conditions, and boundary conditions across files.";
            default -> "";
        };
        return MULTI_FILE_SYSTEM_PROMPT + personaModifier;
    }

    private String buildUserPrompt(MultiFileAnalysisRequest request) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("Analyze this multi-file project:\n\n");

        if (request.getProjectName() != null && !request.getProjectName().isBlank()) {
            prompt.append("Project: ").append(request.getProjectName()).append("\n\n");
        }

        for (FileContent file : request.getFiles()) {
            prompt.append("=== FILE: ").append(file.getPath() != null ? file.getPath() : file.getFilename()).append(" ===\n");
            prompt.append("Language: ").append(file.getLanguage()).append("\n");
            prompt.append("```").append(file.getLanguage()).append("\n");
            prompt.append(file.getContent());
            prompt.append("\n```\n\n");
        }

        if (request.getContext() != null && !request.getContext().isBlank()) {
            prompt.append("Developer's context: ").append(request.getContext()).append("\n\n");
        }

        prompt.append("Analyze all files thoroughly, including cross-file dependencies and architecture.");

        return prompt.toString();
    }

    private Map<String, Object> buildRequest(String systemPrompt, String userPrompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", claudeConfig.getModel());
        request.put("max_tokens", 8192); // More tokens for multi-file
        request.put("system", systemPrompt);
        request.put("messages", List.of(
                Map.of("role", "user", "content", userPrompt)
        ));
        return request;
    }

    private MultiFileAnalysisResponse parseResponse(String responseBody, int fileCount) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentArray = root.path("content");

            if (contentArray.isArray() && !contentArray.isEmpty()) {
                String text = contentArray.get(0).path("text").asText();
                return parseAnalysisJson(text, fileCount);
            }

            return createErrorResponse("Unexpected response format");
        } catch (JsonProcessingException e) {
            log.error("Failed to parse response: {}", e.getMessage());
            return createErrorResponse("Failed to parse AI response");
        }
    }

    private MultiFileAnalysisResponse parseAnalysisJson(String text, int fileCount) {
        try {
            String jsonContent = extractJson(text);
            JsonNode node = objectMapper.readTree(jsonContent);

            List<FileFinding> fileFindings = parseFileFindings(node.path("fileFindings"));
            List<CrossFileIssue> crossFileIssues = parseCrossFileIssues(node.path("crossFileIssues"));
            MultiFileAnalysisResponse.ArchitectureReview archReview = parseArchitectureReview(node.path("architectureReview"));

            int totalFindings = fileFindings.stream()
                    .mapToInt(ff -> ff.getFindings().size())
                    .sum() + crossFileIssues.size();

            return MultiFileAnalysisResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .overallScore(node.path("overallScore").asInt(50))
                    .summary(node.path("summary").asText())
                    .fileFindings(fileFindings)
                    .crossFileIssues(crossFileIssues)
                    .architectureReview(archReview)
                    .analyzedAt(System.currentTimeMillis())
                    .totalFiles(fileCount)
                    .totalFindings(totalFindings)
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse analysis JSON: {}", e.getMessage());
            return createErrorResponse("Failed to parse analysis results");
        }
    }

    private List<FileFinding> parseFileFindings(JsonNode fileFindingsNode) {
        List<FileFinding> fileFindings = new ArrayList<>();
        if (fileFindingsNode.isArray()) {
            for (JsonNode ffNode : fileFindingsNode) {
                List<Finding> findings = new ArrayList<>();
                JsonNode findingsArray = ffNode.path("findings");
                if (findingsArray.isArray()) {
                    for (JsonNode fNode : findingsArray) {
                        findings.add(Finding.builder()
                                .severity(parseSeverity(fNode.path("severity").asText()))
                                .line(fNode.has("line") && !fNode.path("line").isNull() ? fNode.path("line").asInt() : null)
                                .issue(fNode.path("issue").asText())
                                .explanation(fNode.path("explanation").asText())
                                .suggestion(fNode.path("suggestion").asText())
                                .build());
                    }
                }
                fileFindings.add(FileFinding.builder()
                        .filename(ffNode.path("filename").asText())
                        .fileScore(ffNode.path("fileScore").asInt(50))
                        .findings(findings)
                        .build());
            }
        }
        return fileFindings;
    }

    private List<CrossFileIssue> parseCrossFileIssues(JsonNode crossFileNode) {
        List<CrossFileIssue> issues = new ArrayList<>();
        if (crossFileNode.isArray()) {
            for (JsonNode cfNode : crossFileNode) {
                List<String> affectedFiles = new ArrayList<>();
                JsonNode filesArray = cfNode.path("affectedFiles");
                if (filesArray.isArray()) {
                    for (JsonNode f : filesArray) {
                        affectedFiles.add(f.asText());
                    }
                }
                issues.add(CrossFileIssue.builder()
                        .issue(cfNode.path("issue").asText())
                        .explanation(cfNode.path("explanation").asText())
                        .affectedFiles(affectedFiles)
                        .suggestion(cfNode.path("suggestion").asText())
                        .build());
            }
        }
        return issues;
    }

    private MultiFileAnalysisResponse.ArchitectureReview parseArchitectureReview(JsonNode archNode) {
        List<String> strengths = new ArrayList<>();
        List<String> concerns = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();

        if (archNode.path("strengths").isArray()) {
            for (JsonNode s : archNode.path("strengths")) strengths.add(s.asText());
        }
        if (archNode.path("concerns").isArray()) {
            for (JsonNode c : archNode.path("concerns")) concerns.add(c.asText());
        }
        if (archNode.path("recommendations").isArray()) {
            for (JsonNode r : archNode.path("recommendations")) recommendations.add(r.asText());
        }

        return MultiFileAnalysisResponse.ArchitectureReview.builder()
                .overview(archNode.path("overview").asText())
                .strengths(strengths)
                .concerns(concerns)
                .recommendations(recommendations)
                .build();
    }

    private String extractJson(String text) {
        String cleaned = text.trim();
        if (cleaned.startsWith("```json")) {
            cleaned = cleaned.substring(7);
        } else if (cleaned.startsWith("```")) {
            cleaned = cleaned.substring(3);
        }
        if (cleaned.endsWith("```")) {
            cleaned = cleaned.substring(0, cleaned.length() - 3);
        }
        return cleaned.trim();
    }

    private Severity parseSeverity(String value) {
        return switch (value.toLowerCase()) {
            case "critical" -> Severity.CRITICAL;
            case "warning" -> Severity.WARNING;
            default -> Severity.SUGGESTION;
        };
    }

    private MultiFileAnalysisResponse createErrorResponse(String message) {
        return MultiFileAnalysisResponse.builder()
                .id(UUID.randomUUID().toString())
                .overallScore(0)
                .summary("Analysis failed: " + message)
                .fileFindings(List.of())
                .crossFileIssues(List.of())
                .architectureReview(MultiFileAnalysisResponse.ArchitectureReview.builder()
                        .overview("Analysis could not be completed")
                        .strengths(List.of())
                        .concerns(List.of())
                        .recommendations(List.of())
                        .build())
                .analyzedAt(System.currentTimeMillis())
                .totalFiles(0)
                .totalFindings(0)
                .build();
    }
}
