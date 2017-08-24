package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
public class WarehouseData {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Version
    private Integer version;

    /* ******** INPUTS ******** */

    // Mano de obra directa
    @Min(0L)
    private int directWorkforce = 0;

    // Mano de obra indirecta
    @Min(0L)
    private int indirectWorkforce = 0;

    // Area
    private int squareMeters = 0;

    // Vehiculos
    @OneToOne
    @NotNull
    private WarehouseDataVehicles vehicles = new WarehouseDataVehicles();

    // Sistema de almacenamiento
    @OneToOne
    @NotNull
    private WarehouseDataStorage storage = new WarehouseDataStorage();

    // Sistema transportador
    @OneToOne
    @NotNull
    private WarehouseDataConveyor conveyor = new WarehouseDataConveyor();

    /* ******** OUTPUTS ******** */
    @OneToOne
    @NotNull
    private WarehouseDataOutput output = new WarehouseDataOutput();

    @NotNull
    @ManyToOne
    private Warehouse warehouse;

    @NotNull
    private String period;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public int getDirectWorkforce() {
        return directWorkforce;
    }

    public void setDirectWorkforce(int directWorkforce) {
        this.directWorkforce = directWorkforce;
    }

    public int getIndirectWorkforce() {
        return indirectWorkforce;
    }

    public void setIndirectWorkforce(int indirectWorkforce) {
        this.indirectWorkforce = indirectWorkforce;
    }

    public int getSquareMeters() {
        return squareMeters;
    }

    public void setSquareMeters(int squareMeters) {
        this.squareMeters = squareMeters;
    }

    public WarehouseDataVehicles getVehicles() {
        return vehicles;
    }

    public void setVehicles(WarehouseDataVehicles vehicles) {
        this.vehicles = vehicles;
    }

    public WarehouseDataStorage getStorage() {
        return storage;
    }

    public void setStorage(WarehouseDataStorage storage) {
        this.storage = storage;
    }

    public WarehouseDataConveyor getConveyor() {
        return conveyor;
    }

    public void setConveyor(WarehouseDataConveyor conveyor) {
        this.conveyor = conveyor;
    }

    public WarehouseDataOutput getOutput() {
        return output;
    }

    public void setOutput(WarehouseDataOutput output) {
        this.output = output;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public int getTotalWorkforce() {
        return this.directWorkforce + this.indirectWorkforce;
    }

    public double getInputTotalInvestment() {
        return this.storage.getTotalInvestment() + this.vehicles.getTotalInvestment() + this.conveyor.getTotalInvestment();
    }

    public int getOutputAccumulation() {
        int brokenCaseLines = this.output.getBrokenCaseLines();
        int fullCaseLines = this.output.getFullCaseLines();
        int palletLines = this.output.getPalletLines();
        int totalOrders = this.output.getTotalOrders();

        return brokenCaseLines + fullCaseLines + palletLines - totalOrders;
    }

    public double getOutputStorage() {
        int brokenCaseLines = this.output.getBrokenCaseLines();
        int fullCaseLines = this.output.getFullCaseLines();
        int palletLines = this.output.getPalletLines();
        int squareMeters = this.squareMeters;
        int brokenCasePickSlots = this.output.getBrokenCasePickSlots();
        int palletRackLocations = this.output.getPalletRackLocations();
        int flootStackin = this.output.getFloorStacking();

        double p = brokenCaseLines / (brokenCaseLines + fullCaseLines + palletLines);

        return p * Math.sqrt(brokenCasePickSlots + (1 - p) * (1.55 * Math.sqrt(palletRackLocations)) + Math.sqrt(squareMeters * flootStackin));
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @PersistenceContext
    private transient EntityManager entityManager;

    private static EntityManager entityManager() {
        EntityManager em = new WarehouseData().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static WarehouseData findWarehouseData(Long id) {
        if (id == null) return null;
        return entityManager().find(WarehouseData.class, id);
    }

    public static List<WarehouseData> findAllWarehouseData() {
        return entityManager().createQuery("SELECT o FROM WarehouseData o", WarehouseData.class).getResultList();
    }

    @Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }

    @Transactional
    public void remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            WarehouseData attached = findWarehouseData(this.id);
            this.entityManager.remove(attached);
        }
    }

    @Transactional
    public void flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }

    @Transactional
    public void clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }

    @Transactional
    public WarehouseData merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        WarehouseData merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static List<WarehouseData> findWarehouseDataByWarehouse(Warehouse warehouse) {
        if (warehouse == null) throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.warehouse = :warehouse", WarehouseData.class);
        q.setParameter("warehouse", warehouse);

        return q.getResultList();
    }


    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.serialize(this);
    }
}
