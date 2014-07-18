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
    		inversion= this.contrapesoOperadorDePie * DetalleEnum.ContrapesoOperadorDePie.getDetalle() +
    			this.transpaletaElectrica* DetalleEnum.TranspaletaElectrica.getDetalle() +
    			this.contrapesoOperadorDePie * DetalleEnum.ContrapesoOperadorDePie.getDetalle() +
    			this.medioLevante * DetalleEnum.MedioLevante.getDetalle() +
    			this.piernasAHorcagajadas * DetalleEnum.PiernasAhorcagajadas.getDetalle() +
    			this.piernasAhorcajadasYPantografo * DetalleEnum.PiernasAhorcajadasYPantografo.getDetalle() +
    			this.cargaLateral * DetalleEnum.CargaLateral.getDetalle() + 
    			this.conTorreta * DetalleEnum.ConTorreta.getDetalle() +
    			this.vehiculoAlmacenamientoYRecuperacion * DetalleEnum.VehiculoAlmacenamientoYRecuperacion.getDetalle() +
    			this.maquinaRegedoraGuiadaPorRiel * DetalleEnum.MaquinaRegedoraGuiadaPorRiel.getDetalle() +
    			this.maquinaRecogedoraGuiadaPorCable * DetalleEnum.MaquinaRecogedoraGuiadaPorCable.getDetalle() +
    			this.vehiculoGuiadoAutomaticamente * DetalleEnum.VehiculoGuiadoAutomaticamente.getDetalle() +
    			this.carretilla * DetalleEnum.Carretilla.getDetalle() +
    			this.transpaletaManual * DetalleEnum.TranspaletaManual.getDetalle() +
    			this.montaCargasHibrido * DetalleEnum.MontaCargasHibrido.getDetalle()+

	    		this.personaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida * DetalleEnum.PersonaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida.getDetalle()+
	    		this.carruselHorizontal * DetalleEnum.CarruselHorizontal.getDetalle()+
	    		this.carruselVertical * DetalleEnum.CarruselHorizontal.getDetalle()+
	    		this.maquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas * DetalleEnum.MaquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas.getDetalle()+
	    		this.moduloDispensadorAFrame* DetalleEnum.ModuloDispensadorAFrame.getDetalle()+
	    		this.transportadorDeRodillo* DetalleEnum.TransportadorDeRodillo.getDetalle()+
	    		this.transportadorDeRodilloAccionadoOMotorizado* DetalleEnum.TransportadorDeRodilloAccionadoOMotorizado.getDetalle()+
	    		this.transportadorDeCinta* DetalleEnum.TransportadorDeCinta.getDetalle()+
	    		this.transportadorDeRuedas* DetalleEnum.TransportadorDeRuedas.getDetalle()+
	    		this.transportadorDeLineaDeRemolque* DetalleEnum.TransportadorDeRodillo.getDetalle()+
	    		this.clasificadorBandejaMovible* DetalleEnum.ClasificadorBandejaMovible.getDetalle()+
	    		this.transportadorDePallet* DetalleEnum.TransportadorDePallet.getDetalle();

    	}else{
    		inversion= this.inversion;
    	}
    	
    	dato.setInversion(inversion);
    	return dato;
    }
}
