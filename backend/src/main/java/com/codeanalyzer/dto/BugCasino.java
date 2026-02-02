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
public class BugCasino {
    private String sessionId;
    private int karmaBalance; // Virtual currency

    private List<BugBet> availableBets;
    private BugRace currentRace;
    private List<Achievement> casinoAchievements;
    private Leaderboard leaderboard;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BugBet {
        private String bugId;
        private String bugName;
        private String bugType; // "SQL Injection", "Memory Leak", etc.
        private String description;
        private double odds; // 2.5x, 3.0x, etc.
        private int currentBets; // How many people bet on this
        private String favoriteStatus; // "Favorite", "Underdog", "Dark Horse"
        private String icon;
        private List<String> recentForm; // Like horse racing: "W-W-L-W"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BugRace {
        private String raceId;
        private String raceName; // "The Production Derby"
        private String trackConditions; // "Fast (CI skipped)", "Heavy (Code review pending)"
        private List<RaceEntry> entries;
        private String status; // "Betting Open", "Race in Progress", "Photo Finish"
        private int timeToRaceSeconds;
        private String commentary; // Live race commentary
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RaceEntry {
        private String bugId;
        private String bugName;
        private String jockey; // "Rushed Developer", "Junior Dev", "Friday Deploy"
        private int position; // Current position in race
        private String status; // "Leading", "Gaining", "Falling Behind"
        private double currentOdds;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Achievement {
        private String id;
        private String name;
        private String icon;
        private String description;
        private boolean unlocked;
        private int karmaReward;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Leaderboard {
        private List<LeaderboardEntry> topBettors;
        private LeaderboardEntry yourPosition;
        private String title; // "Bug Prophet", "Fortune Teller", etc.
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private int rank;
        private String username;
        private String avatar;
        private int totalKarma;
        private int winStreak;
        private String title;
    }
}
