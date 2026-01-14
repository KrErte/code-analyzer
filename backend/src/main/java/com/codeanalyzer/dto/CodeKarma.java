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
public class CodeKarma {
    private int karmaScore; // -100 to +100
    private String karmaVerdict; // "Code Saint", "Neutral", "Tech Debt Terrorist"

    private TechDebtCreated debtCreated;
    private TechDebtInherited debtInherited;

    private List<KarmaEvent> karmaLedger;
    private String nextLifePrediction; // What happens in your next code review
    private String reincarnationAs; // "You will be reincarnated as a COBOL developer"

    // Future predictions
    private String sixMonthsFromNow;
    private String oneYearFromNow;
    private String futureYouMessage; // Message from future you about this code

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechDebtCreated {
        private int totalHours; // Hours of tech debt you're creating
        private int maintainerCurses; // How many times maintainers will curse you
        private List<String> debtItems; // Specific debts being created
        private String worstOffense;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TechDebtInherited {
        private int totalHours; // Hours of tech debt you inherited
        private String originalSinner; // Who created this mess
        private String yearOfSin; // When the original sin was committed
        private List<String> inheritedProblems;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KarmaEvent {
        private String action; // "Added null check" or "Skipped tests"
        private int karmaPoints; // +5 or -10
        private String consequence; // What this causes
    }
}
