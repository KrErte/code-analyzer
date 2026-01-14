package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UltraAnalysisResponse {
    private String id;
    private long analyzedAt;

    // Core analysis (from enhanced)
    private EnhancedAnalysisResponse coreAnalysis;

    // VIRAL FEATURES - TIER 1
    private BugWeather bugWeather;
    private CodeInsurance codeInsurance;
    private IncidentSimulation incidentSimulation;
    private DeveloperDNA developerDNA;

    // VIRAL FEATURES - TIER 2 (ULTRA WILD)
    private CodeHorrorMovie horrorMovie;
    private TechDebtStock techDebtStock;
    private CodeConfessional confessional;
    private CodeDatingProfile datingProfile;
    private StartupGraveyard startupGraveyard;

    // Shareable cards
    private ShareableCards shareableCards;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShareableCards {
        private String weatherCardUrl;
        private String insuranceQuoteUrl;
        private String dnaCardUrl;
        private String verdictCardUrl;
        private String twitterShareText;
        private String linkedInShareText;
    }
}
