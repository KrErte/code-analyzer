package com.codeanalyzer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Generate a horror movie poster/trailer for your buggy code
 * "THIS SUMMER... ONE NULL POINTER... WILL DESTROY EVERYTHING"
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CodeHorrorMovie {
    private String movieTitle; // "The NullPointer Massacre"
    private String tagline; // "In space, no one can hear your exceptions"
    private String genre; // "Psychological Horror", "Slasher", "Found Footage"
    private String rating; // "Rated P0: Parental Guidance Won't Help"

    private String director; // "Directed by Senior Developer Who Left"
    private String producer; // "Produced by Technical Debt LLC"

    private List<CastMember> cast;
    private String synopsis; // Full movie plot
    private List<String> criticalAcclaim; // Fake reviews

    private TrailerScript trailer;
    private String posterDescription; // Description of what the poster would look like
    private String soundtrackMood; // "Ominous synth, distant screaming servers"

    private BoxOffice boxOffice;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CastMember {
        private String role; // "The NullPointerException"
        private String playedBy; // "Line 47"
        private String motivation; // "Just wants to crash everything"
        private String famousLine; // "I'll be null... forever"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrailerScript {
        private List<TrailerScene> scenes;
        private String voiceoverText; // Deep movie trailer voice
        private String endingStinger; // Final scary moment
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrailerScene {
        private int sceneNumber;
        private String visualDescription;
        private String dialogue;
        private String soundEffect;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BoxOffice {
        private String openingWeekend; // "Lost $2M in the first hour"
        private String totalGross; // "Total damage: $5.7M"
        private int numberOfSequels; // "12 more incidents spawned"
    }
}
