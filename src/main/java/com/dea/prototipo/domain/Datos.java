package com.dea.prototipo.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import javax.validation.constraints.Min;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import javax.persistence.Column;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@RooJpaActiveRecord(finders = { "findDatosesByBodega" })
public class Datos {

    /**
     */
    private double superficie;

    /**
     */
    private double inversion;

    /**
     */
    @Min(0L)
    private double horasDeTrabajoTotal;

    /**
     */
    @Min(0L)
    private int BrokenCaseLines;

    /**
     */
    @Min(0L)
    private int fullCaseLines;

    /**
     */
    @Min(0L)
    private int PalletLines;

    /**
     */
    @Min(0L)
    private double acumulacion;

    /**
     */
    @Min(0L)
    private double almacenamiento;

    /**
     */
    @NotNull
    @ManyToOne
    private Bodega bodega;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date fechaDeRegistro;
}
