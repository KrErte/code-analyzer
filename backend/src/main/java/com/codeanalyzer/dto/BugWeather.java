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
public class BugWeather {
    private String currentCondition; // "Stormy", "Cloudy", "Partly Buggy", "Clear Skies"
    private String icon; // Weather emoji
    private int temperature; // "Bug temperature" 0-100
    private String temperatureFeelsLike; // "Feels like mass resignation"
    private int humidity; // Tech debt humidity
    private String windDirection; // "Bugs blowing in from legacy-service"
    private int windSpeed; // How fast bugs are spreading

    private String advisory; // "SEVERE BUG WARNING", "Bug Watch", etc.
    private String advisoryDetails;

    private List<HourlyForecast> hourlyForecast;
    private List<DailyForecast> weeklyForecast;
    private List<String> warnings;

    private SeasonalPattern seasonalPattern; // "Black Friday Storm Season"

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HourlyForecast {
        private String hour;
        private String condition;
        private String icon;
        private int bugProbability;
        private String event; // "Sprint Demo", "Deploy Window", etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DailyForecast {
        private String day;
        private String condition;
        private String icon;
        private int highBugTemp;
        private int lowBugTemp;
        private int incidentProbability;
        private String specialEvent; // "Patch Tuesday", "Black Friday", etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SeasonalPattern {
        private String currentSeason; // "Q4 Crunch", "Summer Intern Season"
        private String historicalNote; // "87% of incidents happen in Q4"
        private List<String> upcomingDangerDates;
    }
}
