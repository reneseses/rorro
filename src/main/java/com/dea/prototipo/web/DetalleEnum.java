package com.dea.prototipo.web;

public enum DetalleEnum {
ContraPesoSentado(30000d), TranspaletaElectrica(10000d), ContrapesoOperadorDePie(30000d),MedioLevante(8000d),PiernasAHorcagajadas(35000d),PiernasAHorcajadasYPantografo(45000d),CargaLateral(65000d),ConTorreta(80000d),VehiculoAlmacenamientoYRecuperacion(150000d),MaquinaRegedoraGuiadaPorRiel(30000d),MaquinaRecogedoraGuiadaPorCable(35000d),VehículoGuiadoAutomaticamente(80000d),Carretilla(644d),TranspaletaManual(3000d),MontaCargasHibrido(100000d);
    
    private double detalle;
	private DetalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
