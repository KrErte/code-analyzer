package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Code Confessional - Confess your coding sins
 * "Forgive me Claude, for I have sinned. I pushed to main on Friday at 5:47 PM."
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeConfessional {
    private String confessionId;
    private String sinner; // Anonymous

    private List<Sin> sinsDetected;
    private int totalSinPoints;
    private String sinnerLevel; // "Venial Sinner", "Mortal Sinner", "Tech Debt Satan"

    private Penance penance;
    private String absolution; // What you need to do for forgiveness
    private String priestResponse; // Claude's response as priest

    private SinHistory sinHistory; // Pattern of sins

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Sin {
        private String sinName; // "Skipping Code Review"
        private String category; // "Sloth", "Pride", "Greed"
        private int severity; // 1-10
        private String confession; // Auto-generated confession text
        private String verse; // "And on the third day, the tests failed" (Biblical style)
        private String evidence; // Line number / code snippet
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Penance {
        private List<String> tasks; // "Write 10 unit tests"
        private int hailMarys; // "Say 5 'Hail Proper Error Handling'"
        private String pilgrimage; // "Journey through the legacy codebase"
        private String donation; // "Donate 2 hours to documentation"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SinHistory {
        private int lifetimeSins;
        private String mostFrequentSin;
        private String redemptionPath; // "Path to enlightenment: Learn TDD"
        private boolean atRiskOfDamnation; // Job security concern
    }
}
