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
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AnalysisController {

    private final ClaudeService claudeService;
    private final MultiFileAnalysisService multiFileAnalysisService;
    private final EnhancedAnalysisService enhancedAnalysisService;

    // Simple in-memory store for shared analyses (in production: use Redis/DB)
    private final Map<String, EnhancedAnalysisResponse> analysisStore = new ConcurrentHashMap<>();

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

    // ============ CI/CD INTEGRATION ENDPOINTS ============

    /**
     * CI/CD Gate endpoint - returns pass/fail for pipeline integration
     * Use in GitHub Actions, GitLab CI, Jenkins, etc.
     * Exit code 0 = pass, Exit code 1 = fail
     */
    @PostMapping("/ci/gate")
    public Mono<ResponseEntity<Map<String, Object>>> ciGate(@Valid @RequestBody AnalysisRequest request) {
        log.info("CI Gate check for {} code", request.getLanguage());

        return enhancedAnalysisService.analyzeEnhanced(
                request.getCode(),
                request.getLanguage(),
                request.getContext(),
                "brutal"
        ).map(analysis -> {
            String verdict = analysis.getShipItScore() != null
                ? analysis.getShipItScore().getVerdict() : "UNKNOWN";
            boolean passed = verdict.contains("SHIP IT") || verdict.contains("MAYBE");
            int criticalCount = (int) analysis.getFindings().stream()
                .filter(f -> f.getSeverity().name().equals("CRITICAL")).count();

            Map<String, Object> response = Map.of(
                "passed", passed,
                "verdict", verdict,
                "score", analysis.getScore(),
                "criticalIssues", criticalCount,
                "incidentCount", analysis.getPredictedIncidents().size(),
                "karmaScore", analysis.getCodeKarma() != null ? analysis.getCodeKarma().getKarmaScore() : 0,
                "recommendation", passed ? "Safe to merge" : "BLOCK: Fix critical issues before merging",
                "analysisId", analysis.getId()
            );

            return passed ? ResponseEntity.ok(response)
                : ResponseEntity.status(422).body(response);
        });
    }

    /**
     * Quick verdict - minimal response for CLI tools
     */
    @PostMapping("/ci/quick")
    public Mono<ResponseEntity<Map<String, Object>>> quickVerdict(@Valid @RequestBody AnalysisRequest request) {
        return enhancedAnalysisService.analyzeEnhanced(
                request.getCode(),
                request.getLanguage(),
                request.getContext(),
                "brutal"
        ).map(analysis -> {
            String verdict = analysis.getShipItScore() != null
                ? analysis.getShipItScore().getVerdict() : "UNKNOWN";
            String emoji = verdict.contains("SHIP IT") ? "✅" :
                          verdict.contains("MAYBE") ? "⚠️" :
                          verdict.contains("NOPE") ? "❌" : "💀";

            return ResponseEntity.ok(Map.of(
                "verdict", emoji + " " + verdict,
                "score", analysis.getScore(),
                "tldr", analysis.getShipItScore() != null ? analysis.getShipItScore().getTldr() : "Analysis failed"
            ));
        });
    }

    // ============ SHAREABLE ANALYSIS ENDPOINTS ============

    /**
     * Store analysis for sharing
     */
    @PostMapping("/share")
    public Mono<ResponseEntity<Map<String, String>>> shareAnalysis(@Valid @RequestBody AnalysisRequest request) {
        return enhancedAnalysisService.analyzeEnhanced(
                request.getCode(),
                request.getLanguage(),
                request.getContext(),
                request.getPersona()
        ).map(analysis -> {
            analysisStore.put(analysis.getId(), analysis);
            return ResponseEntity.ok(Map.of(
                "shareId", analysis.getId(),
                "shareUrl", "/share/" + analysis.getId(),
                "expiresIn", "24 hours"
            ));
        });
    }

    /**
     * Get shared analysis by ID
     */
    @GetMapping("/share/{id}")
    public ResponseEntity<EnhancedAnalysisResponse> getSharedAnalysis(@PathVariable String id) {
        EnhancedAnalysisResponse analysis = analysisStore.get(id);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(analysis);
    }

    // ============ BADGE ENDPOINTS ============

    /**
     * Generate SVG badge for README files
     */
    @GetMapping("/badge/{shareId}")
    public ResponseEntity<String> getBadge(@PathVariable String shareId) {
        EnhancedAnalysisResponse analysis = analysisStore.get(shareId);
        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }

        String verdict = analysis.getShipItScore() != null
            ? analysis.getShipItScore().getVerdict().replaceAll("[^a-zA-Z\\s]", "") : "Unknown";
        String color = verdict.contains("SHIP IT") ? "brightgreen" :
                      verdict.contains("MAYBE") ? "yellow" :
                      verdict.contains("NOPE") ? "red" : "critical";
        int karma = analysis.getCodeKarma() != null ? analysis.getCodeKarma().getKarmaScore() : 0;

        String svg = String.format("""
            <svg xmlns="http://www.w3.org/2000/svg" width="200" height="20">
              <linearGradient id="b" x2="0" y2="100%%">
                <stop offset="0" stop-color="#bbb" stop-opacity=".1"/>
                <stop offset="1" stop-opacity=".1"/>
              </linearGradient>
              <mask id="a"><rect width="200" height="20" rx="3" fill="#fff"/></mask>
              <g mask="url(#a)">
                <rect width="80" height="20" fill="#555"/>
                <rect x="80" width="120" height="20" fill="%s"/>
              </g>
              <g fill="#fff" font-family="DejaVu Sans,sans-serif" font-size="11">
                <text x="6" y="14">Code Karma</text>
                <text x="86" y="14">%d | %s</text>
              </g>
            </svg>
            """, color, karma, verdict.trim());

        return ResponseEntity.ok()
            .header("Content-Type", "image/svg+xml")
            .body(svg);
    }
}
