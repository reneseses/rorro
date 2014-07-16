package com.dea.prototipo.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.OneToMany;
import javax.persistence.Column;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.UniqueConstraint;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findBodegasByUsuario" })
public class Bodega {

    /**
     */
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "bodega")
    private Set<Datos> datos = new HashSet<Datos>();

    /**
     */
    /**
     */
    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoOperacion tipoOperacion;

    @ManyToOne
    private Usuario usuario;

    /**
     */
    @NotNull
    @Column(unique = true)
    private String nombreBodega;
}
