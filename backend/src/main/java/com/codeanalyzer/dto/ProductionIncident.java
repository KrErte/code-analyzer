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
public class ProductionIncident {
    private String id;
    private String title;
    private String severity; // P0, P1, P2, P3
    private String scenario; // "2AM traffic spike", "Database failover", etc.
    private String whatHappens; // Detailed description of the failure
    private String rootCause; // Link to specific code
    private Integer affectedLine;
    private String affectedFile;
    private String timeToOccur; // "Immediately", "Within days", "Within months"
    private int probabilityPercent; // 0-100
    private String businessImpact; // "Users can't checkout", "Data loss"
    private CostEstimate costEstimate;
    private String preventionCode; // Fixed code snippet

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CostEstimate {
        private int minDollars;
        private int maxDollars;
        private String breakdown; // "Engineering: $2k, Lost revenue: $50k, Customer trust: priceless"
    }
}
