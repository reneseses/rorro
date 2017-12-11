package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

@Configurable
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"warehouse", "period"})
)
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

    public Double getOutputStorage() {
        double brokenCaseLines = (double) this.output.getBrokenCaseLines();
        int fullCaseLines = this.output.getFullCaseLines();
        int palletLines = this.output.getPalletLines();
        int squareMeters = this.squareMeters;
        int brokenCasePickSlots = this.output.getBrokenCasePickSlots();
        int palletRackLocations = this.output.getPalletRackLocations();
        int floorStacking = this.output.getFloorStacking();

        if (brokenCaseLines + fullCaseLines + palletLines == 0) {
            return null;
        }

        double p = brokenCaseLines / (brokenCaseLines + fullCaseLines + palletLines);

        double result = p * Math.sqrt(brokenCasePickSlots) + (1 - p) * (1.55 * Math.sqrt(palletRackLocations) + Math.sqrt(squareMeters * floorStacking));

        return Math.round(result * 1000) / 1000d;
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

    @Transactional
    public void update() {
        if (this.id == null) throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        Query q = em.createQuery("UPDATE WarehouseData SET period= :period, squareMeters= :squareMeters, directWorkforce= :directWorkforce, indirectWorkforce= :indirectWorkforce WHERE id = :id");
        q.setParameter("period", this.period);
        q.setParameter("squareMeters", this.squareMeters);
        q.setParameter("directWorkforce", this.directWorkforce);
        q.setParameter("indirectWorkforce", this.indirectWorkforce);
        q.setParameter("id", this.id);

        q.executeUpdate();
    }

    public static List<WarehouseData> findWarehouseDataByWarehouse(Warehouse warehouse) {
        if (warehouse == null) throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.warehouse = :warehouse", WarehouseData.class);
        q.setParameter("warehouse", warehouse);

        return q.getResultList();
    }

    public static List<WarehouseData> findWarehouseDataByPeriod(String period) {
        if (period == null)
            throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.period = :period", WarehouseData.class);
        q.setParameter("period", period);

        return q.getResultList();
    }

    public static WarehouseData findWarehouseDataByWarehouseAndPeriod(Warehouse warehouse, String period) {
        if (warehouse == null || period == null)
            throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.warehouse = :warehouse AND o.period = :period", WarehouseData.class);
        q.setParameter("warehouse", warehouse);
        q.setParameter("period", period);

        return q.getSingleResult();
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("entityManager", "warehouse", "vehicles", "storage", "conveyor", "output");
        return serializer.serialize(this);
    }
}
