package com.dea.prototipo.web;

public enum detalleEnum {
ContraPesoSentado(30000d), TranspaletaElectrica(10000d), ContrapesoOperadorDePie(30000d);
    
    private double detalle;
	private detalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
