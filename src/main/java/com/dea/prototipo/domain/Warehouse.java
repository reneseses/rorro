package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@RooJavaBean
@RooToString
@Configurable
@Entity
@Table(
        uniqueConstraints = @UniqueConstraint(columnNames = {"name", "user"})
)
public class Warehouse {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     */
    @NotNull
    private String name;


    @Version
    private Integer version;

    /**
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private OperationType operationType;

    /**
     */
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TILevel tiLevel;

    @ManyToOne
    private User user;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return this.version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public OperationType getOperationType() {
        return operationType;
    }

    public void setOperationType(OperationType operationType) {
        this.operationType = operationType;
    }

    public ProductType getProductType() {
        return this.productType;
    }

    public void setProductType(ProductType productType) {
        this.productType = productType;
    }

    public TILevel getTiLevel() {
        return this.tiLevel;
    }

    public void setTiLevel(TILevel tiLevel) {
        this.tiLevel = tiLevel;
    }

    public User getUser() {
        return this.user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @PersistenceContext
    transient EntityManager entityManager;

    public static final List<String> fieldNames4OrderClauseFilter = java.util.Arrays.asList("productType", "user", "name");

    public static final EntityManager entityManager() {
        EntityManager em = new Warehouse().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static long countWarehouses() {
        return entityManager().createQuery("SELECT COUNT(o) FROM Warehouse o", Long.class).getSingleResult();
    }

    public static List<Warehouse> findAllWarehouses() {
        return entityManager().createQuery("SELECT o FROM Warehouse o", Warehouse.class).getResultList();
    }

    public static List<Warehouse> findAllWarehouses(String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Warehouse o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Warehouse.class).getResultList();
    }

    public static Warehouse findWarehouse(Long id) {
        if (id == null) return null;
        return entityManager().find(Warehouse.class, id);
    }

    public static List<Warehouse> findWarehouseEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM Warehouse o", Warehouse.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }

    public static List<Warehouse> findWarehouseEntries(int firstResult, int maxResults, String sortFieldName, String sortOrder) {
        String jpaQuery = "SELECT o FROM Warehouse o";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        return entityManager().createQuery(jpaQuery, Warehouse.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
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
            Warehouse attached = Warehouse.findWarehouse(this.id);
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
    public void update() {
        if (this.id == null) throw new IllegalArgumentException("The warehouse argument is required");
        EntityManager em = entityManager();
        Query q = em.createQuery("UPDATE Warehouse SET name= :name, operationType= :operationType, tiLevel= :tiLevel, productType= :productType WHERE id = :id");
        q.setParameter("name", this.name);
        q.setParameter("operationType", this.operationType);
        q.setParameter("tiLevel", this.tiLevel);
        q.setParameter("productType", this.productType);
        q.setParameter("id", this.id);

        q.executeUpdate();
    }

    @Transactional
    public Warehouse merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Warehouse merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public static Long countWarehousesByUser(User user) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Warehouse.entityManager();
        TypedQuery q = em.createQuery("SELECT COUNT(o) FROM Warehouse AS o WHERE o.user = :user", Long.class);
        q.setParameter("user", user);
        return ((Long) q.getSingleResult());
    }

    public static TypedQuery<Warehouse> findWarehousesByUser(User user) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Warehouse.entityManager();
        TypedQuery<Warehouse> q = em.createQuery("SELECT o FROM Warehouse AS o WHERE o.user = :user", Warehouse.class);
        q.setParameter("user", user);
        return q;
    }

    public static List<Warehouse> findWarehousesByUser(User user, OperationType operationType, ProductType productType, TILevel tiLevel) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        String qString = "SELECT o FROM Warehouse AS o WHERE o.user = :user";

        if (operationType != null) {
            qString += " AND o.operationType= :operationType";
        }

        if (productType != null) {
            qString += " AND o.productType= :productType";
        }

        if (tiLevel != null) {
            qString += " AND o.tiLevel= :tiLevel";
        }

        EntityManager em = Warehouse.entityManager();
        TypedQuery<Warehouse> q = em.createQuery(qString, Warehouse.class);
        q.setParameter("user", user);
        if (operationType != null) {
            q.setParameter("operationType", operationType);
        }

        if (productType != null) {
            q.setParameter("productType", productType);
        }

        if (tiLevel != null) {
            q.setParameter("tiLevel", tiLevel);
        }
        return q.getResultList();
    }

    public static TypedQuery<Warehouse> findWarehousesByUser(User user, String sortFieldName, String sortOrder) {
        if (user == null) throw new IllegalArgumentException("The user argument is required");
        EntityManager em = Warehouse.entityManager();
        String jpaQuery = "SELECT o FROM 'Warehouse' AS o WHERE o.user = :user";
        if (fieldNames4OrderClauseFilter.contains(sortFieldName)) {
            jpaQuery = jpaQuery + " ORDER BY " + sortFieldName;
            if ("ASC".equalsIgnoreCase(sortOrder) || "DESC".equalsIgnoreCase(sortOrder)) {
                jpaQuery = jpaQuery + " " + sortOrder;
            }
        }
        TypedQuery<Warehouse> q = em.createQuery(jpaQuery, Warehouse.class);
        q.setParameter("user", user);
        return q;
    }

    public static List<Warehouse> findWarehouseByTypes(OperationType operationType, ProductType productType, TILevel tiLevel) {
        if (operationType == null && productType == null && tiLevel == null)
            throw new IllegalArgumentException("The user argument is required");

        String qString = "SELECT o FROM Warehouse AS o WHERE";

        if (operationType != null) {
            qString += " o.operationType= :operationType";
        }

        if (productType != null) {
            if (operationType != null) {
                qString += " AND";
            }
            qString += " o.productType= :productType";
        }

        if (tiLevel != null) {
            if (operationType != null || productType != null) {
                qString += " AND";
            }
            qString += " o.tiLevel= :tiLevel";
        }

        EntityManager em = Warehouse.entityManager();
        TypedQuery<Warehouse> q = em.createQuery(qString, Warehouse.class);

        if (operationType != null) {
            q.setParameter("operationType", operationType);
        }

        if (productType != null) {
            q.setParameter("productType", productType);
        }

        if (tiLevel != null) {
            q.setParameter("tiLevel", tiLevel);
        }
        return q.getResultList();
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("entityManager");
        return serializer.serialize(this);
    }
}
