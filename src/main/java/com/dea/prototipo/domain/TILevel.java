package com.dea.prototipo.domain;

public enum TILevel {
    VERYLOW("Muy Bajo", "Automatización básica, como el uso de un computador."),
    LOW("Bajo", "Solamente uso de WMS"),
    AVERAGE("Promedio", "WMS y tecnología código de barras"),
    HIGH("Alto", "WMS, tecnología de código de barras y radiofrecuencia"),
    VERYHIGH("Muy Alto", "WMS, tecnología de código de barras, radiofrecuencia y sistemas automatizados");

    private String label;
    private String description;

    TILevel(String label, String description) {
        this.label = label;
        this.description = description;
    }

    public String getName() {
        return this.label;
    }

    public String getDescription() {
        return this.description;
    }
}
