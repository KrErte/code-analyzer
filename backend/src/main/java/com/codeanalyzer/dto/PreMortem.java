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
public class PreMortem {
    private String incidentTitle;
    private String severity;
    private String date; // Future date when it happens
    private String timeOfIncident;
    private String duration;

    // The postmortem written BEFORE it happens
    private String executiveSummary;
    private String timeline;
    private List<String> rootCauses;
    private List<String> contributingFactors;
    private String impactAssessment;
    private String customerCommunication; // The apology email template
    private List<String> actionItems;
    private String lessonsLearned;

    // Meta
    private String whoGetsBlamed;
    private String slackChannelName; // #incident-your-code-broke-prod
    private int numberOfPagesGenerated;
    private String postmortemMeetingDuration;
}
