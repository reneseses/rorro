package com.dea.prototipo.domain;

public enum OperationType {
    Manufacture("Manufactura"),
    Retail("Retail"),
    Wholesaler("Mayorista"),
    VirtualStore("Tienda virtual");

    private String label;

    OperationType(String label) {
        this.label = label;
    }

    public String getName() {
        return this.label;
    }
}
