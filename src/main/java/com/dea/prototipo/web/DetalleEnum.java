package com.dea.prototipo.web;

public enum DetalleEnum {
contraPesoSentado(30000d),
transpaletaElectrica(10000d),
contrapesoOperadorDePie(30000d),
medioLevante(8000d),
piernasAhorcagajadas(35000d),
piernasAhorcajadasYPantografo(45000d),
cargaLateral(65000d),
conTorreta(80000d),
vehiculoAlmacenamientoYRecuperacion(150000d),
maquinaRegedoraGuiadaPorRiel(30000d),
maquinaRecogedoraGuiadaPorCable(35000d),
vehiculoGuiadoAutomaticamente(80000d),
carretilla(644d),
transpaletaManual(3000d),
montaCargasHibrido(100000d),
personaABordoDeLaMaquinaAutomatizadaDeAlmacenamientoYRecogida(110000d),
carruselHorizontal(40000d),
carruselVertical(65000d),
maquinaAutomatizadaDeAlmacenamientoYRecogidaParaMinicargas(125000d),
moduloDispensadorAFrame(750d),
transportadorDeRodillo(50d),
transportadorDeRodilloAccionadoOMotorizado(200d),
transportadorDeCinta(100d),
transportadorDeRuedas(25d),
transportadorDeLineaDeRemolque(100d),
clasificadorBandejaMovible(1000d),
transportadorDePallet(1000d);

    
    private double detalle;
	private DetalleEnum(double detalle){
		this.detalle= detalle;
	}
	public double getDetalle(){
		return this.detalle;
	}
}
