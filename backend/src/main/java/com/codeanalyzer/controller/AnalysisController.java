package com.codeanalyzer.controller;

import com.codeanalyzer.dto.AnalysisRequest;
import com.codeanalyzer.dto.AnalysisResponse;
import com.codeanalyzer.dto.MultiFileAnalysisRequest;
import com.codeanalyzer.dto.MultiFileAnalysisResponse;
import com.codeanalyzer.service.ClaudeService;
import com.codeanalyzer.service.MultiFileAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final ClaudeService claudeService;
    private final MultiFileAnalysisService multiFileAnalysisService;

    @PostMapping("/analyze")
    public Mono<ResponseEntity<AnalysisResponse>> analyzeCode(@Valid @RequestBody AnalysisRequest request) {
        log.info("Received analysis request for {} code with {} persona",
                request.getLanguage(), request.getPersona());

        return claudeService.analyzeCode(
                        request.getCode(),
                        request.getLanguage(),
                        request.getContext(),
                        request.getPersona())
                .map(ResponseEntity::ok);
    }

    @PostMapping("/analyze/multi")
    public Mono<ResponseEntity<MultiFileAnalysisResponse>> analyzeMultipleFiles(
            @Valid @RequestBody MultiFileAnalysisRequest request) {
        log.info("Received multi-file analysis request for {} files", request.getFiles().size());

        return multiFileAnalysisService.analyzeMultipleFiles(request)
                .map(ResponseEntity::ok);
    }

    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status", "healthy",
                "service", "code-analyzer"
        ));
    }

    @GetMapping("/personas")
    public ResponseEntity<Map<String, Object>> getPersonas() {
        return ResponseEntity.ok(Map.of(
                "personas", java.util.List.of(
                        Map.of(
                                "id", "brutal",
                                "name", "Brutal Senior",
                                "description", "Harshly critical, finds every possible flaw. No mercy."
                        ),
                        Map.of(
                                "id", "mentor",
                                "name", "Constructive Mentor",
                                "description", "Critical but educational. Explains the 'why' behind issues."
                        ),
                        Map.of(
                                "id", "edge-hunter",
                                "name", "Edge Case Hunter",
                                "description", "Focuses specifically on boundary conditions and edge cases."
                        )
                )
        ));
    }

    @GetMapping("/languages")
    public ResponseEntity<Map<String, Object>> getLanguages() {
        return ResponseEntity.ok(Map.of(
                "languages", java.util.List.of(
                        Map.of("id", "java", "name", "Java"),
                        Map.of("id", "javascript", "name", "JavaScript"),
                        Map.of("id", "typescript", "name", "TypeScript"),
                        Map.of("id", "python", "name", "Python")
                )
        ));
    }
}
