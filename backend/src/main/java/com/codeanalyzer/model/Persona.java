package com.codeanalyzer.model;

public enum Persona {
    BRUTAL("brutal"),
    MENTOR("mentor"),
    EDGE_HUNTER("edge-hunter");

    private final String value;

    Persona(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Persona fromValue(String value) {
        for (Persona persona : values()) {
            if (persona.value.equalsIgnoreCase(value)) {
                return persona;
            }
        }
        throw new IllegalArgumentException("Unknown persona: " + value);
    }
}
