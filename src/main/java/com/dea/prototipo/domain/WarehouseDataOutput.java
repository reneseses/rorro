package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;

@Configurable
@Entity
public class WarehouseDataOutput {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Ordenes totales
     */
    @Min(0L)
    private int totalOrders = 0;

    /**
     * Lineas caja abierta despachadas
     */
    @Min(0L)
    private int brokenCaseLines = 0;

    /**
     * Lineas caja completa despachadas
     */
    @Min(0L)
    private int fullCaseLines = 0;

    /**
     * Lineas pallet despachadas
     */
    @Min(0L)
    private int palletLines = 0;

    /**
     * Lineas pallet despachadas
     */
    @Min(0L)
    private int brokenCasePickSlots = 0;

    /**
     * Lineas pallet despachadas
     */
    @Min(0L)
    private int floorStacking = 0;

    /**
     * Lineas pallet despachadas
     */
    @Min(0L)
    private int palletRackLocations = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public int getBrokenCaseLines() {
        return brokenCaseLines;
    }

    public void setBrokenCaseLines(int brokenCaseLines) {
        this.brokenCaseLines = brokenCaseLines;
    }

    public int getFullCaseLines() {
        return fullCaseLines;
    }

    public void setFullCaseLines(int fullCaseLines) {
        this.fullCaseLines = fullCaseLines;
    }

    public int getPalletLines() {
        return palletLines;
    }

    public void setPalletLines(int palletLines) {
        this.palletLines = palletLines;
    }

    public int getBrokenCasePickSlots() {
        return brokenCasePickSlots;
    }

    public void setBrokenCasePickSlots(int brokenCasePickSlots) {
        this.brokenCasePickSlots = brokenCasePickSlots;
    }

    public int getFloorStacking() {
        return floorStacking;
    }

    public void setFloorStacking(int floorStacking) {
        this.floorStacking = floorStacking;
    }

    public int getPalletRackLocations() {
        return palletRackLocations;
    }

    public void setPalletRackLocations(int palletRackLocations) {
        this.palletRackLocations = palletRackLocations;
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.serialize(this);
    }

}
