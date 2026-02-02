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
public class ShipItScore {
    private String verdict; // "SHIP IT 🚀", "MAYBE 🤔", "NOPE 🔥", "ARE YOU SERIOUS? 💀"
    private int confidence; // 0-100
    private String reasoning;
    private List<String> mustFixBefore;
    private List<String> niceToHave;
    private String tldr; // One-liner summary
    private RiskBreakdown riskBreakdown;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RiskBreakdown {
        private int securityRisk;
        private int stabilityRisk;
        private int performanceRisk;
        private int maintainabilityRisk;
        private int dataLossRisk;
    }
}
