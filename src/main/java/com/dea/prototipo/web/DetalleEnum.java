package com.dea.prototipo.web;

public enum DetalleEnum {
ContraPesoSentado(30000d), TranspaletaElectrica(10000d), ContrapesoOperadorDePie(30000d);
    
    private double detalle;
	private DetalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
