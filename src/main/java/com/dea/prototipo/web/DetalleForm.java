package com.dea.prototipo.web;

import java.util.Date;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.roo.addon.javabean.RooJavaBean;

import com.dea.prototipo.domain.Bodega;
import com.dea.prototipo.domain.Datos;

@RooJavaBean
public class DetalleForm {
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
    
    boolean detalle=false;
    
    private double contraPesoSentado=0;
    
    private double transpaletaElectrica=0;
    
    private double contrapesoOperadorDePie=0;
    
    private double medioLevante=0;
    
    private double piernasAHorcagajadas=0;
    
    private double piernasAhorcajadasYPantografo=0;
    
    private double cargaLateral=0;

    private double conTorreta=0;
    
    private double vehiculoAlmacenamientoYRecuperacion=0;
    
    private double maquinaRegedoraGuiadaPorRiel=0;
    //cambio
    private double maquinaRecogedoraGuiadaPorCable=0;
    
    private double vehiculoGuiadoAutomaticamente=0;
    
    private double carretilla=0;
        
    private double transpaletaManual=0;
    
    private double montaCargasHibrido=0;
    
    private double personaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida=0;
    
    private double carruselHorizontal=0;
    
    private double carruselVertical=0;
    
    private double maquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas=0;
    
    private double moduloDispensadorAFrame=0;
    
    private double transportadorDeRodillo=0;
    
    private double transportadorDeRodilloAccionadoOMotorizado=0;
    
    private double transportadorDeCinta=0;
    
    private double transportadorDeRuedas=0;
    
    private double transportadorDeLineaDeRemolque=0;
    
    private double clasificadorBandejaMovible=0;
    
    private double transportadorDePallet=0;
    
    public Datos getDatos(){
    	Datos dato= new Datos();
    	dato.setAcumulacion(this.acumulacion);
    	dato.setAlmacenamiento(this.almacenamiento);
    	dato.setBodega(this.bodega);
    	dato.setBrokenCaseLines(this.BrokenCaseLines);
    	dato.setFechaDeRegistro(this.fechaDeRegistro);
    	dato.setFullCaseLines(this.fullCaseLines);
    	dato.setHorasDeTrabajoTotal(this.horasDeTrabajoTotal);
    	dato.setPalletLines(this.PalletLines);
    	dato.setSuperficie(this.superficie);
    	
    	
    	Double inversion=0d;
    	if(this.detalle){
    		inversion= this.contrapesoOperadorDePie * DetalleEnum.contrapesoOperadorDePie.getDetalle() +
    			this.transpaletaElectrica* DetalleEnum.transpaletaElectrica.getDetalle() +
    			this.contrapesoOperadorDePie * DetalleEnum.contrapesoOperadorDePie.getDetalle() +
    			this.medioLevante * DetalleEnum.medioLevante.getDetalle() +
    			this.piernasAHorcagajadas * DetalleEnum.piernasAhorcagajadas.getDetalle() +
    			this.piernasAhorcajadasYPantografo * DetalleEnum.piernasAhorcajadasYPantografo.getDetalle() +
    			this.cargaLateral * DetalleEnum.cargaLateral.getDetalle() + 
    			this.conTorreta * DetalleEnum.conTorreta.getDetalle() +
    			this.vehiculoAlmacenamientoYRecuperacion * DetalleEnum.vehiculoAlmacenamientoYRecuperacion.getDetalle() +
    			this.maquinaRegedoraGuiadaPorRiel * DetalleEnum.maquinaRegedoraGuiadaPorRiel.getDetalle() +
    			this.maquinaRecogedoraGuiadaPorCable * DetalleEnum.maquinaRecogedoraGuiadaPorCable.getDetalle() +
    			this.vehiculoGuiadoAutomaticamente * DetalleEnum.vehiculoGuiadoAutomaticamente.getDetalle() +
    			this.carretilla * DetalleEnum.carretilla.getDetalle() +
    			this.transpaletaManual * DetalleEnum.transpaletaManual.getDetalle() +
    			this.montaCargasHibrido * DetalleEnum.montaCargasHibrido.getDetalle()+
	    		this.personaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida * DetalleEnum.personaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida.getDetalle()+
	    		this.carruselHorizontal * DetalleEnum.carruselHorizontal.getDetalle()+
	    		this.carruselVertical * DetalleEnum.carruselHorizontal.getDetalle()+
	    		this.maquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas * DetalleEnum.maquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas.getDetalle()+
	    		this.moduloDispensadorAFrame* DetalleEnum.moduloDispensadorAFrame.getDetalle()+
	    		this.transportadorDeRodillo* DetalleEnum.transportadorDeRodillo.getDetalle()+
	    		this.transportadorDeRodilloAccionadoOMotorizado* DetalleEnum.transportadorDeRodilloAccionadoOMotorizado.getDetalle()+
	    		this.transportadorDeCinta* DetalleEnum.transportadorDeCinta.getDetalle()+
	    		this.transportadorDeRuedas* DetalleEnum.transportadorDeRuedas.getDetalle()+
	    		this.transportadorDeLineaDeRemolque* DetalleEnum.transportadorDeRodillo.getDetalle()+
	    		this.clasificadorBandejaMovible* DetalleEnum.clasificadorBandejaMovible.getDetalle()+
	    		this.transportadorDePallet* DetalleEnum.transportadorDePallet.getDetalle();
    	}else{
    		inversion= this.inversion;
    	}
    	
    	dato.setInversion(inversion);
    	return dato;
    }
}
