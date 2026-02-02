package com.codeanalyzer.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Severity {
    CRITICAL("critical"),
    WARNING("warning"),
    SUGGESTION("suggestion");

    private final String value;

    Severity(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
