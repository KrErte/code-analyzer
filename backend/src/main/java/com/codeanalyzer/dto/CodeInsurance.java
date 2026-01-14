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
public class CodeInsurance {
    private String policyNumber;
    private String riskTier; // "Extreme", "High", "Moderate", "Low", "Uninsurable"

    private Quote monthlyQuote;
    private Quote annualQuote;

    private int deductible; // Per-incident deductible
    private int coverageLimit; // Max payout

    private List<String> coveredIncidents;
    private List<String> exclusions; // "Friday deploys", "Intern code", etc.

    private List<RiskFactor> riskFactors;
    private List<Discount> availableDiscounts;

    private String underwriterNotes; // "We've seen some things..."
    private String claimsHistory; // Based on code patterns

    private Comparison industryComparison;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Quote {
        private int premium;
        private String currency;
        private String breakdown; // "Base: $X, N+1 surcharge: $Y"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskFactor {
        private String factor;
        private String icon;
        private int impactPercent; // How much it raises premium
        private String explanation;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Discount {
        private String name;
        private int percentOff;
        private String requirement; // "Add circuit breakers"
        private boolean applied;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Comparison {
        private int yourPremium;
        private int industryAverage;
        private int topPerformers;
        private String verdict; // "3x more expensive than well-written code"
    }
}
