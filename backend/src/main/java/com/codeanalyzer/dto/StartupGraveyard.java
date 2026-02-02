package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Match your code patterns to failed startups
 * "Your authentication logic matches Theranos security practices"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StartupGraveyard {
    private List<GraveyardMatch> matches;
    private String overallRiskAssessment;
    private String survivalPrediction;
    private String investorWarning;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GraveyardMatch {
        private String startupName; // "Theranos", "WeWork Tech", "Quibi"
        private String yearOfDeath;
        private String causeOfDeath; // "Technical", "Business", "Fraud"
        private int similarityPercent;

        private String matchedPattern; // What code pattern matches
        private String theirMistake; // What they did wrong
        private String yourMistake; // What you're doing that's similar

        private String epitaph; // "Here lies good intentions and bad code"
        private String tombstoneEmoji;

        private FailureDetails failureDetails;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailureDetails {
        private String peakValuation;
        private String finalValuation; // Usually $0
        private String moneyBurned;
        private String employeesAffected;
        private String infamousQuote; // "We're not a tech company, we're a lifestyle"
        private List<String> redFlagsIgnored;
    }

    // Famous failed startups to match against
    public static List<FailedStartup> getFailedStartups() {
        return List.of(
            FailedStartup.builder()
                .name("Theranos")
                .pattern("Security through obscurity")
                .mistake("Fake it till you make it (but with blood tests)")
                .epitaph("Here lies 'revolutionary' technology that never worked")
                .build(),
            FailedStartup.builder()
                .name("WeWork Tech")
                .pattern("Over-engineering simple problems")
                .mistake("$47B valuation for a subletting company with an app")
                .epitaph("Community-adjusted losses forever")
                .build(),
            FailedStartup.builder()
                .name("Quibi")
                .pattern("Building features nobody asked for")
                .mistake("$1.75B to learn people rotate their phones")
                .epitaph("Quick bites, quick death")
                .build(),
            FailedStartup.builder()
                .name("Juicero")
                .pattern("Over-complicating simple solutions")
                .mistake("$400 machine to squeeze juice bags")
                .epitaph("You could just... use your hands")
                .build(),
            FailedStartup.builder()
                .name("MoviePass")
                .pattern("Unsustainable business logic")
                .mistake("Unlimited movies for $10/month. What could go wrong?")
                .epitaph("The math never mathed")
                .build()
        );
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FailedStartup {
        private String name;
        private String pattern;
        private String mistake;
        private String epitaph;
    }
}
