package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EnhancedAnalysisResponse {
    // Original fields
    private String id;
    private int score;
    private List<Finding> findings;
    private String improvedCode;
    private String summary;
    private long analyzedAt;

    // NEW: Production Incident Simulation
    private List<ProductionIncident> predictedIncidents;
    private String incidentTimeline; // Visual ASCII timeline

    // NEW: Ship-It Score
    private ShipItScore shipItScore;

    // NEW: Cost Analysis
    private CostAnalysis costAnalysis;

    // NEW: Achievements
    private List<Achievement> achievements;

    // NEW: Roast (if roast mode)
    private CodeRoast roast;

    // NEW: Famous Bug Pattern Matching
    private List<FamousBugMatch> famousBugMatches;

    // NEW: Pre-Mortem (postmortem before the incident)
    private PreMortem preMortem;

    // NEW: On-Call Forecast
    private OnCallForecast onCallForecast;

    // NEW: Code Karma
    private CodeKarma codeKarma;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FamousBugMatch {
        private String famousBugId;
        private String bugName;
        private String company;
        private String year;
        private String icon;
        private int similarityPercent;
        private String matchReason;
        private String financialImpact;
        private String yourCodePattern; // Snippet from their code
        private String historyPattern; // What it's similar to
        private String lesson;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostAnalysis {
        private int totalEstimatedCost;
        private int engineeringHoursToFix;
        private int potentialRevenueLoss;
        private int technicalDebtCost;
        private String recommendation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodeRoast {
        private String headline; // "This code is why we can't have nice things"
        private List<String> roasts; // Individual roast lines
        private String memeUrl; // Generated meme reference
        private int savageryLevel; // 1-10
        private String constructiveTakeaway; // Actual helpful advice after the roast
    }
}
