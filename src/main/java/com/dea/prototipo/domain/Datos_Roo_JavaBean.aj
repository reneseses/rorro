// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.dea.prototipo.domain;

import com.dea.prototipo.domain.Bodega;
import com.dea.prototipo.domain.Datos;
import java.util.Date;

privileged aspect Datos_Roo_JavaBean {
    
    public double Datos.getSuperficie() {
        return this.superficie;
    }
    
    public void Datos.setSuperficie(double superficie) {
        this.superficie = superficie;
    }
    
    public double Datos.getInversion() {
        return this.inversion;
    }
    
    public void Datos.setInversion(double inversion) {
        this.inversion = inversion;
    }
    
    public double Datos.getHorasDeTrabajoTotal() {
        return this.horasDeTrabajoTotal;
    }
    
    public void Datos.setHorasDeTrabajoTotal(double horasDeTrabajoTotal) {
        this.horasDeTrabajoTotal = horasDeTrabajoTotal;
    }
    
    public int Datos.getBrokenCaseLines() {
        return this.BrokenCaseLines;
    }
    
    public void Datos.setBrokenCaseLines(int BrokenCaseLines) {
        this.BrokenCaseLines = BrokenCaseLines;
    }
    
    public int Datos.getFullCaseLines() {
        return this.fullCaseLines;
    }
    
    public void Datos.setFullCaseLines(int fullCaseLines) {
        this.fullCaseLines = fullCaseLines;
    }
    
    public int Datos.getPalletLines() {
        return this.PalletLines;
    }
    
    public void Datos.setPalletLines(int PalletLines) {
        this.PalletLines = PalletLines;
    }
    
    public double Datos.getAcumulacion() {
        return this.acumulacion;
    }
    
    public void Datos.setAcumulacion(double acumulacion) {
        this.acumulacion = acumulacion;
    }
    
    public double Datos.getAlmacenamiento() {
        return this.almacenamiento;
    }
    
    public void Datos.setAlmacenamiento(double almacenamiento) {
        this.almacenamiento = almacenamiento;
    }
    
    public Bodega Datos.getBodega() {
        return this.bodega;
    }
    
    public void Datos.setBodega(Bodega bodega) {
        this.bodega = bodega;
    }
    
    public Date Datos.getFechaDeRegistro() {
        return this.fechaDeRegistro;
    }
    
    public void Datos.setFechaDeRegistro(Date fechaDeRegistro) {
        this.fechaDeRegistro = fechaDeRegistro;
    }
    
}
