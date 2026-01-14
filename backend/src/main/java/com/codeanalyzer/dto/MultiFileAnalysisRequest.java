package com.codeanalyzer.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MultiFileAnalysisRequest {

    @NotEmpty(message = "At least one file is required")
    @Size(max = 20, message = "Maximum 20 files allowed")
    private List<FileContent> files;

    @Size(max = 1000, message = "Context must not exceed 1000 characters")
    private String context;

    private String persona;

    private String projectName;
}
