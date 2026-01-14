package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeveloperDNA {
    private String devId;
    private String personalityType; // "INTJ-NullPointer", "ENFP-Spaghetti"
    private String spiritAnimal; // "Chaos Monkey", "Rubber Duck", etc.
    private String alignment; // D&D style: "Chaotic Evil (moves fast, breaks things)"

    private DNABreakdown codeOrigins;
    private CodingStyle codingStyle;
    private List<Ancestor> codingAncestors; // Famous devs you code like

    private CompatibilityReport teamCompatibility;
    private CareerPrediction careerPrediction;

    private String shareableCard; // Generated image/card

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DNABreakdown {
        private int stackOverflowPercent;
        private int tutorialCodePercent;
        private int actualEngineeringPercent;
        private int prayersAndHopesPercent;
        private int legacyCopyPastePercent;
        private int aiGeneratedPercent;
        private Map<String, Integer> otherSources;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CodingStyle {
        private String primaryStyle; // "The Architect", "The Hacker", "The Perfectionist"
        private String secondaryStyle;
        private List<String> strengths;
        private List<String> weaknesses;
        private String debuggingApproach; // "Print statements", "Debugger", "Pray"
        private String documentationHabit; // "What's documentation?"
        private String testingPhilosophy; // "If it compiles, ship it"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Ancestor {
        private String name; // "Linus Torvalds", "Grace Hopper"
        private String trait; // What you inherited
        private int similarityPercent;
        private String quote; // Famous quote
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CompatibilityReport {
        private String idealPartner; // Dev type you should work with
        private String avoidAtAllCosts; // Dev type that will clash
        private List<TeamDynamic> teamDynamics;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TeamDynamic {
        private String devType;
        private int compatibilityScore;
        private String prediction; // "Will argue about tabs vs spaces"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CareerPrediction {
        private String fiveYears; // "Staff Engineer or burnout"
        private String tenYears; // "CTO or goat farmer"
        private String peakTitle; // "Distinguished Engineer (if you fix your null checks)"
        private String alternativeCareer; // "Surprisingly good at woodworking"
    }
}
