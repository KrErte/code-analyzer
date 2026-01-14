package com.codeanalyzer.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnalysisRequest {

    @NotBlank(message = "Code is required")
    @Size(max = 50000, message = "Code must not exceed 50000 characters")
    private String code;

    @NotBlank(message = "Language is required")
    @Pattern(regexp = "^(java|javascript|typescript|python)$", message = "Invalid language")
    private String language;

    @Size(max = 1000, message = "Context must not exceed 1000 characters")
    private String context;

    @NotBlank(message = "Persona is required")
    @Pattern(regexp = "^(brutal|mentor|edge-hunter)$", message = "Invalid persona")
    private String persona;
}
