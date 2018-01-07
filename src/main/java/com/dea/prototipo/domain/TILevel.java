package com.dea.prototipo.domain;

public enum TILevel {
    VERYLOW("Muy Bajo"),
    LOW("Bajo"),
    AVERAGE("Promedio"),
    HIGH("Alto"),
    VERYHIGH("Muy Alto");

    private String label;

    TILevel(String label) {
        this.label = label;
    }

    public String getName() {
        return this.label;
    }
}
