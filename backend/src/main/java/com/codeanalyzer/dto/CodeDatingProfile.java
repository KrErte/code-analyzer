package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Dating profile for your code
 * "Single NullPointerException looking for try-catch block"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeDatingProfile {
    private String profileId;
    private String displayName; // "Legacy Larry", "Spaghetti Sarah"
    private int age; // Years since first commit
    private String location; // "Living in production, visiting staging"

    private String bio; // "Just a misunderstood function looking for love"
    private String lookingFor; // "Seeking: Proper error handling"

    private List<String> interests; // "Long walks through stack traces"
    private List<String> turnOns; // "Good documentation", "Proper typing"
    private List<String> turnOffs; // "Global variables", "Magic numbers"
    private List<String> redFlags; // Issues detected
    private List<String> greenFlags; // Good patterns

    private Stats stats;
    private List<Review> reviews; // Reviews from other code
    private MatchPrediction matchPrediction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Stats {
        private int linesOfCode;
        private int complexity; // "It's complicated"
        private int dependencies; // "Has baggage"
        private int testCoverage; // "Commitment level"
        private String relationshipStatus; // "Tightly coupled", "Loosely coupled", "It's complicated"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Review {
        private String reviewer; // "Previous Maintainer"
        private int rating; // 1-5 stars
        private String review; // "2/5 - Would not merge again"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MatchPrediction {
        private String idealMatch; // "Well-tested microservice"
        private String worstMatch; // "Another monolith"
        private int compatibilityScore;
        private String warning; // "May cause merge conflicts"
    }
}
