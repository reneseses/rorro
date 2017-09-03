package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
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
     * SKU Caja abierta
     */
    @Min(0L)
    private int brokenCasePickSlots = 0;

    /**
     * Almacenamiento en el suelo
     */
    @Min(0L)
    private int floorStacking = 0;

    /**
     * Ubicaciones pallet
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

    @PersistenceContext
    private transient EntityManager entityManager;

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    private static EntityManager entityManager() {
        EntityManager em = new WarehouseDataOutput().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public WarehouseDataOutput merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        WarehouseDataOutput merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("entityManager");
        return serializer.serialize(this);
    }

}
