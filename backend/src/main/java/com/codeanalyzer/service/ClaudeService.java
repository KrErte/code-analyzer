package com.codeanalyzer.service;

import com.codeanalyzer.config.ClaudeConfig;
import com.codeanalyzer.dto.AnalysisResponse;
import com.codeanalyzer.dto.Finding;
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
public class ClaudeService {

    private final WebClient claudeWebClient;
    private final ClaudeConfig claudeConfig;
    private final PromptTemplateService promptTemplateService;
    private final ObjectMapper objectMapper;

    public Mono<AnalysisResponse> analyzeCode(String code, String language, String context, String personaValue) {
        Persona persona = Persona.fromValue(personaValue);
        String systemPrompt = promptTemplateService.getSystemPrompt(persona);
        String userPrompt = promptTemplateService.buildUserPrompt(code, language, context);

        Map<String, Object> requestBody = buildRequest(systemPrompt, userPrompt);

        log.debug("Sending request to Claude API with persona: {}", persona);

        return claudeWebClient.post()
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseResponse)
                .doOnError(e -> log.error("Error calling Claude API: {}", e.getMessage()))
                .onErrorResume(e -> Mono.just(createErrorResponse(e.getMessage())));
    }

    private Map<String, Object> buildRequest(String systemPrompt, String userPrompt) {
        Map<String, Object> request = new HashMap<>();
        request.put("model", claudeConfig.getModel());
        request.put("max_tokens", claudeConfig.getMaxTokens());
        request.put("system", systemPrompt);
        request.put("messages", List.of(
                Map.of("role", "user", "content", userPrompt)
        ));
        return request;
    }

    private AnalysisResponse parseResponse(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode contentArray = root.path("content");

            if (contentArray.isArray() && !contentArray.isEmpty()) {
                String text = contentArray.get(0).path("text").asText();
                return parseAnalysisJson(text);
            }

            log.error("Unexpected response structure: {}", responseBody);
            return createErrorResponse("Unexpected response format from AI");
        } catch (JsonProcessingException e) {
            log.error("Failed to parse Claude response: {}", e.getMessage());
            return createErrorResponse("Failed to parse AI response");
        }
    }

    private AnalysisResponse parseAnalysisJson(String text) {
        try {
            // Extract JSON from the response (handle markdown code blocks if present)
            String jsonContent = extractJson(text);
            JsonNode analysisNode = objectMapper.readTree(jsonContent);

            List<Finding> findings = new ArrayList<>();
            JsonNode findingsArray = analysisNode.path("findings");

            if (findingsArray.isArray()) {
                for (JsonNode findingNode : findingsArray) {
                    Finding finding = Finding.builder()
                            .severity(parseSeverity(findingNode.path("severity").asText()))
                            .line(findingNode.has("line") && !findingNode.path("line").isNull()
                                    ? findingNode.path("line").asInt() : null)
                            .issue(findingNode.path("issue").asText())
                            .explanation(findingNode.path("explanation").asText())
                            .suggestion(findingNode.path("suggestion").asText())
                            .build();
                    findings.add(finding);
                }
            }

            return AnalysisResponse.builder()
                    .id(UUID.randomUUID().toString())
                    .score(analysisNode.path("score").asInt(50))
                    .summary(analysisNode.path("summary").asText())
                    .findings(findings)
                    .improvedCode(analysisNode.path("improvedCode").asText())
                    .analyzedAt(System.currentTimeMillis())
                    .build();

        } catch (JsonProcessingException e) {
            log.error("Failed to parse analysis JSON: {}. Raw text: {}", e.getMessage(), text);
            return createErrorResponse("Failed to parse analysis results");
        }
    }

    private String extractJson(String text) {
        // Remove markdown code blocks if present
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

    private AnalysisResponse createErrorResponse(String message) {
        return AnalysisResponse.builder()
                .id(UUID.randomUUID().toString())
                .score(0)
                .summary("Analysis failed: " + message)
                .findings(List.of())
                .improvedCode("")
                .analyzedAt(System.currentTimeMillis())
                .build();
    }
}
