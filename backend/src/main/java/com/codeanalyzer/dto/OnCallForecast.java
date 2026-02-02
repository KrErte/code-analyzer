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
public class OnCallForecast {
    private int painIndex; // 0-100, how painful your on-call will be
    private String overallVerdict; // "Peaceful", "Rough", "Nightmare", "Career-Ending"
    private int predictedPages; // Number of pages in next 30 days
    private int sleepInterruptions; // Middle-of-night alerts
    private int weekendRuined; // Weekend pages

    private List<OnCallEvent> timeline; // Day by day forecast
    private List<String> survivalTips;

    private String worstCaseScenario;
    private String bestCaseScenario;

    // Fun stats
    private int coffeeCupsNeeded;
    private int grayHairsGained;
    private double relationshipStrainIndex;
    private String recommendedCopingMechanism;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OnCallEvent {
        private String day; // "Day 3", "Day 15"
        private String time; // "3:47 AM"
        private String event; // "NullPointerException storm"
        private String severity; // "P0", "P1", etc
        private String mood; // "Panicked", "Annoyed", "Resigned"
        private String whatYoullBeDoing; // "Explaining to VP why everything is on fire"
    }
}
