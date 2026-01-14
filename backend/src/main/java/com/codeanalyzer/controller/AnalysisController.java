package com.codeanalyzer.controller;

import com.codeanalyzer.dto.*;
import com.codeanalyzer.service.ClaudeService;
import com.codeanalyzer.service.EnhancedAnalysisService;
import com.codeanalyzer.service.MultiFileAnalysisService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final ClaudeService claudeService;
    private final MultiFileAnalysisService multiFileAnalysisService;
    private final EnhancedAnalysisService enhancedAnalysisService;

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

    @PostMapping("/analyze/enhanced")
    public Mono<ResponseEntity<EnhancedAnalysisResponse>> analyzeEnhanced(@Valid @RequestBody AnalysisRequest request) {
        log.info("Enhanced analysis for {} code with {} persona", request.getLanguage(), request.getPersona());

        return enhancedAnalysisService.analyzeEnhanced(
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
                "service", "code-analyzer",
                "version", "2.0-enhanced"
        ));
    }

    @GetMapping("/personas")
    public ResponseEntity<Map<String, Object>> getPersonas() {
        return ResponseEntity.ok(Map.of(
                "personas", List.of(
                        Map.of(
                                "id", "brutal",
                                "name", "Brutal Senior",
                                "description", "Harshly critical, finds every possible flaw. No mercy.",
                                "icon", "🔥"
                        ),
                        Map.of(
                                "id", "mentor",
                                "name", "Constructive Mentor",
                                "description", "Critical but educational. Explains the 'why' behind issues.",
                                "icon", "🎓"
                        ),
                        Map.of(
                                "id", "edge-hunter",
                                "name", "Edge Case Hunter",
                                "description", "Focuses specifically on boundary conditions and edge cases.",
                                "icon", "🎯"
                        ),
                        Map.of(
                                "id", "roast",
                                "name", "Code Roast 🔥",
                                "description", "Savage, meme-worthy roasts. Will hurt. Will be funny. Will teach.",
                                "icon", "💀"
                        )
                )
        ));
    }

    @GetMapping("/languages")
    public ResponseEntity<Map<String, Object>> getLanguages() {
        return ResponseEntity.ok(Map.of(
                "languages", List.of(
                        Map.of("id", "java", "name", "Java"),
                        Map.of("id", "javascript", "name", "JavaScript"),
                        Map.of("id", "typescript", "name", "TypeScript"),
                        Map.of("id", "python", "name", "Python")
                )
        ));
    }

    @GetMapping("/achievements")
    public ResponseEntity<List<Achievement>> getAchievements() {
        return ResponseEntity.ok(Achievement.getAvailableAchievements());
    }

    @GetMapping("/mock-scenarios")
    public ResponseEntity<List<MockScenario>> getMockScenarios() {
        return ResponseEntity.ok(MockScenario.getAll());
    }

    @GetMapping("/mock-scenarios/{id}")
    public ResponseEntity<MockScenario> getMockScenario(@PathVariable String id) {
        return MockScenario.getAll().stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/famous-bugs")
    public ResponseEntity<List<FamousBug>> getFamousBugs() {
        return ResponseEntity.ok(FamousBug.getAll());
    }

    @GetMapping("/famous-bugs/{id}")
    public ResponseEntity<FamousBug> getFamousBug(@PathVariable String id) {
        return FamousBug.getAll().stream()
                .filter(b -> b.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
