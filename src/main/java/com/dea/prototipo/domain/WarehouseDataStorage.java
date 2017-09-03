package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Configurable
@Entity
public class WarehouseDataStorage {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Transelevador (ASRS) hombre a bordo
     */
    @Min(0L)
    private int personAboardASRSAisles = 0;

    /**
     * Carrusel Horizontal
     */
    @Min(0L)
    private int horizontalCarousel = 0;

    /**
     * Carrusel Vertical
     */
    @Min(0L)
    private int verticalCarousel = 0;

    /**
     * ASRS de Minicarga
     */
    @Min(0L)
    private int miniloadASRSAisles = 0;

    /**
     * Dispensador A-Frame
     */
    @Min(0L)
    private int aFrameDispenser = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPersonAboardASRSAisles() {
        return personAboardASRSAisles;
    }

    public void setPersonAboardASRSAisles(int personAboardASRSAisles) {
        this.personAboardASRSAisles = personAboardASRSAisles;
    }

    public int getHorizontalCarousel() {
        return horizontalCarousel;
    }

    public void setHorizontalCarousel(int horizontalCarousel) {
        this.horizontalCarousel = horizontalCarousel;
    }

    public int getVerticalCarousel() {
        return verticalCarousel;
    }

    public void setVerticalCarousel(int verticalCarousel) {
        this.verticalCarousel = verticalCarousel;
    }

    public int getMiniloadASRSAisles() {
        return miniloadASRSAisles;
    }

    public void setMiniloadASRSAisles(int miniloadASRSAisles) {
        this.miniloadASRSAisles = miniloadASRSAisles;
    }

    public int getaFrameDispenser() {
        return aFrameDispenser;
    }

    public void setaFrameDispenser(int aFrameDispenser) {
        this.aFrameDispenser = aFrameDispenser;
    }

    public int getTotalInvestment() {
        return this.personAboardASRSAisles * InputWeightEnum.PersonAboardASRSAisles.getWeight() +
                this.horizontalCarousel * InputWeightEnum.HorizontalCarousel.getWeight() +
                this.verticalCarousel * InputWeightEnum.VerticalCarousel.getWeight() +
                this.miniloadASRSAisles * InputWeightEnum.MiniloadASRSAisles.getWeight() +
                this.aFrameDispenser * InputWeightEnum.AFrameDispenser.getWeight();
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
        EntityManager em = new WarehouseDataStorage().entityManager;
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
    public WarehouseDataStorage merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        WarehouseDataStorage merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("entityManager");
        return serializer.serialize(this);
    }
}
