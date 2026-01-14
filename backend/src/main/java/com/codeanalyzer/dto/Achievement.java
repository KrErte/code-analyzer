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
public class Achievement {
    private String id;
    private String name;
    private String icon;
    private String description;
    private boolean unlocked;
    private String unlockedReason;

    // Predefined achievements
    public static List<Achievement> getAvailableAchievements() {
        return List.of(
            Achievement.builder()
                .id("null_pointer_ninja")
                .name("Null Pointer Ninja")
                .icon("🥷")
                .description("Code has zero null pointer risks")
                .build(),
            Achievement.builder()
                .id("edge_lord")
                .name("Edge Lord")
                .icon("🗡️")
                .description("All edge cases properly handled")
                .build(),
            Achievement.builder()
                .id("security_guardian")
                .name("Security Guardian")
                .icon("🛡️")
                .description("No security vulnerabilities detected")
                .build(),
            Achievement.builder()
                .id("clean_coder")
                .name("Clean Coder")
                .icon("✨")
                .description("Score under 20 - production ready")
                .build(),
            Achievement.builder()
                .id("survivor")
                .name("Survivor")
                .icon("🏕️")
                .description("Submitted code with 80+ bug score and lived")
                .build(),
            Achievement.builder()
                .id("first_blood")
                .name("First Blood")
                .icon("🩸")
                .description("First analysis completed")
                .build(),
            Achievement.builder()
                .id("roasted")
                .name("Roasted")
                .icon("🔥")
                .description("Survived a Code Roast session")
                .build(),
            Achievement.builder()
                .id("learner")
                .name("Fast Learner")
                .icon("📚")
                .description("Improved score by 30+ points on resubmit")
                .build(),
            Achievement.builder()
                .id("multi_file_master")
                .name("Multi-File Master")
                .icon("📁")
                .description("Analyzed 10+ files at once")
                .build(),
            Achievement.builder()
                .id("zero_incidents")
                .name("Zero Incidents")
                .icon("🎯")
                .description("No predicted production incidents")
                .build()
        );
    }
}
