package com.dea.prototipo.domain;

import org.imgscalr.Scalr;
import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Entity
@Configurable
public class WarehouseImage {

    /**
     */
    @Id
    private Long id;

    /**
     */
    @NotNull
    @Lob
    @Basic(fetch = FetchType.LAZY)
    private byte[] content;

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(content));
            Rectangle rect = original.getData().getBounds();
            Double mayor = rect.getHeight();
            if (mayor > rect.getWidth())
                mayor = rect.getWidth();

            BufferedImage dest = Scalr.crop(original, 0, 0, mayor.intValue(), mayor.intValue());

            rect = dest.getData().getBounds();
            Double width = rect.getWidth();

            if (width > 150) {
                dest = Scalr.resize(dest, 150);
            }

            ImageIO.write(dest, "jpg", baos);

            this.content = baos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public void setContent(byte[] content, Integer x, Integer y, Integer w, Integer h) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            BufferedImage original = ImageIO.read(new ByteArrayInputStream(content));

            w = w > original.getWidth() ? original.getWidth() : w;
            h = h > original.getHeight() ? original.getHeight() : h;

            BufferedImage dest = Scalr.crop(original, x, y, w, h);

            Rectangle rect = dest.getData().getBounds();
            Double width = rect.getWidth();

            if (width > 150) {
                dest = Scalr.resize(dest, 150);
            }

            ImageIO.write(dest, "jpg", baos);

            this.content = baos.toByteArray();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
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
        EntityManager em = new WarehouseImage().entityManager;
        if (em == null)
            throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }

    public static WarehouseImage findWarehouseImage(Long id) {
        if (id == null) return null;
        return entityManager().find(WarehouseImage.class, id);
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
            WarehouseImage attached = findWarehouseImage(this.id);
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
    public WarehouseImage merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        WarehouseImage merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }

    @Transactional
    public void update() {
        if (this.id == null) throw new IllegalArgumentException("The id field is required");
        EntityManager em = entityManager();
        Query q = em.createQuery("UPDATE WarehouseData SET content= :content WHERE id = :id");
        q.setParameter("content", this.content);
        q.setParameter("id", this.id);

        q.executeUpdate();

    }
}
