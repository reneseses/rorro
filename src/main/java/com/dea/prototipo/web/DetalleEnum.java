package com.dea.prototipo.web;

public enum DetalleEnum {
ContraPesoSentado(30000d),
TranspaletaElectrica(10000d),
ContrapesoOperadorDePie(30000d),
MedioLevante(8000d),
PiernasAhorcagajadas(35000d),
PiernasAhorcajadasYPantografo(45000d),
CargaLateral(65000d),
ConTorreta(80000d),
VehiculoAlmacenamientoYRecuperacion(150000d),
MaquinaRegedoraGuiadaPorRiel(30000d),
MaquinaRecogedoraGuiadaPorCable(35000d),
VehiculoGuiadoAutomaticamente(80000d),
Carretilla(644d),
TranspaletaManual(3000d),
MontaCargasHibrido(100000d),
PersonaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida(110000d),
CarruselHorizontal(40000d),
CarruselVertical(65000d),
MaquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas(125000d),
ModuloDispensadorAFrame(750d),
TransportadorDeRodillo(50d),
TransportadorDeRodilloAccionadoOMotorizado(200d),
TransportadorDeCinta(100d),
TransportadorDeRuedas(25d),
TransportadorDeLineaDeRemolque(100d),
ClasificadorBandejaMovible(1000d),
TransportadorDePallet(1000d);

    
    private double detalle;
	private DetalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
