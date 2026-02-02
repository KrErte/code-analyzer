package com.codeanalyzer.dto;

import com.codeanalyzer.model.Severity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Finding {
    private Severity severity;
    private Integer line;
    private String issue;
    private String explanation;
    private String suggestion;
}
