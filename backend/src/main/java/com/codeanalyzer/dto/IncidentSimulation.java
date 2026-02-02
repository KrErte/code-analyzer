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
public class IncidentSimulation {
    private String incidentId;
    private String codename; // "Operation Database Fire"
    private String severity; // P0, P1, P2
    private String triggerEvent; // What caused it

    private List<SlackMessage> slackTimeline;
    private List<PagerDutyAlert> pagerDutyAlerts;
    private List<Tweet> twitterReactions;
    private List<Email> emailChain;
    private StatusPageUpdate statusPage;

    private IncidentMetrics metrics;
    private PostmortemDraft postmortemDraft;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SlackMessage {
        private String timestamp;
        private String channel; // "#incidents", "#engineering", "#ceo-direct"
        private String sender;
        private String senderRole; // "On-Call Engineer", "VP Engineering", "CEO"
        private String avatar; // Emoji
        private String message;
        private List<String> reactions; // [":fire:", ":scream:", ":this-is-fine:"]
        private int threadReplies;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PagerDutyAlert {
        private String timestamp;
        private String alertName;
        private String severity;
        private String service;
        private String assignedTo;
        private String status; // "Triggered", "Acknowledged", "Resolved"
        private int escalationLevel;
        private String sound; // "air-raid", "default", "persistent"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Tweet {
        private String handle;
        private String displayName;
        private boolean verified;
        private String tweet;
        private int likes;
        private int retweets;
        private String sentiment; // "angry", "mocking", "supportive"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Email {
        private String timestamp;
        private String from;
        private String fromTitle;
        private String to;
        private String subject;
        private String body;
        private String tone; // "concerned", "furious", "passive-aggressive"
        private boolean ccedLegal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusPageUpdate {
        private String status; // "Major Outage", "Partial Outage", "Degraded"
        private String message;
        private List<String> affectedServices;
        private String lastUpdated;
        private List<String> updateHistory;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IncidentMetrics {
        private int timeToDetectMinutes;
        private int timeToAcknowledgeMinutes;
        private int timeToMitigateMinutes;
        private int timeToResolveMinutes;
        private int customersAffected;
        private int supportTickets;
        private int angryTweets;
        private double revenueImpact;
        private double stockPriceImpact; // For public companies
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PostmortemDraft {
        private String title;
        private String severity;
        private String summary;
        private String rootCause;
        private List<String> timeline;
        private List<String> actionItems;
        private String blamelessAnalysis; // Who to blame (blameless-ly)
        private String lessonsLearned;
    }
}
