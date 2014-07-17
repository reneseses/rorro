package com.dea.prototipo.web;

public enum detalleEnum {
ContraPesoSentado("30000"), MOTIVO2("Motivo 2"), Motivo3("Motivo 3");
    
    private String reason;
	private detalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
