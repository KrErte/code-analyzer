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
public class MultiFileAnalysisResponse {
    private String id;
    private int overallScore;
    private String summary;
    private List<FileFinding> fileFindings;
    private List<CrossFileIssue> crossFileIssues;
    private ArchitectureReview architectureReview;
    private long analyzedAt;
    private int totalFiles;
    private int totalFindings;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ArchitectureReview {
        private String overview;
        private List<String> strengths;
        private List<String> concerns;
        private List<String> recommendations;
    }
}
