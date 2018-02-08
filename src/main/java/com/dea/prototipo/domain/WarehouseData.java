package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
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
    private Integer period;

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

    public Integer getPeriod() {
        return period;
    }

    public void setPeriod(Integer period) {
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

    public static List<WarehouseData> findWarehouseDataByPeriod(Integer period) {
        if (period == null)
            throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.period = :period", WarehouseData.class);
        q.setParameter("period", period);

        return q.getResultList();
    }

    public static WarehouseData findWarehouseDataByWarehouseAndPeriod(Warehouse warehouse, Integer period) {
        if (warehouse == null || period == null)
            throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.warehouse = :warehouse AND o.period = :period", WarehouseData.class);
        q.setParameter("warehouse", warehouse);
        q.setParameter("period", period);

        return q.getSingleResult();
    }

    public static List<WarehouseData> findWarehouseDataByWarehouseAndPeriodBetween(Warehouse warehouse, Integer begin, Integer end) {
        if (warehouse == null || end == null || begin == null)
            throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        TypedQuery<WarehouseData> q = em.createQuery("SELECT o FROM WarehouseData AS o WHERE o.warehouse = :warehouse AND o.period BETWEEN :begin AND :finish", WarehouseData.class);
        q.setParameter("warehouse", warehouse);
        q.setParameter("begin", begin);
        q.setParameter("finish", end);

        return q.getResultList();
    }

    public List<WarehouseData> findBenchmarking(String mode, boolean sameOperationType, boolean sameProductType, boolean sameTILevel, boolean samePeriod) {
        Integer period = this.period;
        Long warehouse = this.warehouse.getId();
        Long user = this.warehouse.getUser().getId();
        EntityManager em = entityManager();
        Query q = null;
        List<WarehouseData> list = new ArrayList<>();
        if (mode.equals("self")) {
            q = em.createNativeQuery("SELECT * FROM warehouse_data AS w1 WHERE w1.warehouse = :warehouse AND w1.period != :period", WarehouseData.class);
            q.setParameter("warehouse", warehouse);
            q.setParameter("period", period);
        } else {
            List<Warehouse> warehouses = new ArrayList<>();
            if (mode.equals("default")) {
                warehouses = Warehouse.findWarehouseByTypes(
                        sameOperationType ? this.warehouse.getOperationType() : null,
                        sameProductType ? this.warehouse.getProductType() : null,
                        sameTILevel ? this.warehouse.getTiLevel() : null);
            } else if (mode.equals("user")) {
                warehouses = Warehouse.findWarehousesByUser(
                        this.warehouse.getUser(),
                        sameOperationType ? this.warehouse.getOperationType() : null,
                        sameProductType ? this.warehouse.getProductType() : null,
                        sameTILevel ? this.warehouse.getTiLevel() : null);
            }
            List<Long> ids = new ArrayList<>();
            for (Warehouse wh : warehouses) {
                if (!wh.getId().equals(warehouse))
                    ids.add(wh.getId());
            }
            if (ids.size() > 0) {
                String query = "SELECT w1.id, w1.direct_workforce, w1.indirect_workforce, w1.period, w1.square_meters, w1.version, w1.conveyor, w1.output, w1.storage, w1.vehicles, w1.warehouse" +
                        " FROM warehouse_data AS w1 LEFT JOIN warehouse_data AS w2" +
                        " ON (w1.warehouse = w2.warehouse AND w1.period < w2.period)" +
                        " WHERE w2.id IS NULL AND w1.warehouse != :warehouse AND w1.warehouse IN (:ids)";

                if (samePeriod) {
                    query = "SELECT * FROM warehouse_data AS w1" +
                            " WHERE w1.warehouse != :warehouse AND w1.warehouse IN (:ids) AND w1.period= :period";
                }

                q = em.createNativeQuery(query, WarehouseData.class);
                q.setParameter("warehouse", warehouse);
                q.setParameter("ids", ids);

                if (samePeriod) {
                    q.setParameter("period", period);
                }
            }
        }

        if (q != null) {
            list = q.getResultList();
        }
        return list;
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("entityManager", "warehouse", "vehicles", "storage", "conveyor", "output");
        return serializer.serialize(this);
    }
}
