package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.Min;

@Configurable
@Entity
public class WarehouseDataConveyor {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Cinta de Rodillos
     */
    @Min(0L)
    private double nonPoweredRoller = 0;

    /**
     * Cinta de Rodillos Motorizada
     */
    @Min(0L)
    private double poweredRollerConveyor = 0;

    /**
     * Cinta de Banda Motorizada
     */
    @Min(0L)
    private double powerBelt = 0;

    /**
     * Cinta de Ruedas
     */
    @Min(0L)
    private double skateWheel = 0;

    /**
     * Tow Line (cinta por cable enterrado)
     */
    @Min(0L)
    private double towLine = 0;

    /**
     * Clasificador Tilt Tray
     */
    @Min(0L)
    private double tiltTraySorter = 0;

    /**
     * Cinta Transportadora de Pallet
     */
    @Min(0L)
    private double palletConveyor = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public double getNonPoweredRoller() {
        return nonPoweredRoller;
    }

    public void setNonPoweredRoller(double nonPoweredRoller) {
        this.nonPoweredRoller = nonPoweredRoller;
    }

    public double getPoweredRollerConveyor() {
        return poweredRollerConveyor;
    }

    public void setPoweredRollerConveyor(double poweredRollerConveyor) {
        this.poweredRollerConveyor = poweredRollerConveyor;
    }

    public double getPowerBelt() {
        return powerBelt;
    }

    public void setPowerBelt(double powerBelt) {
        this.powerBelt = powerBelt;
    }

    public double getSkateWheel() {
        return skateWheel;
    }

    public void setSkateWheel(double skateWheel) {
        this.skateWheel = skateWheel;
    }

    public double getTowLine() {
        return towLine;
    }

    public void setTowLine(double towLine) {
        this.towLine = towLine;
    }

    public double getTiltTraySorter() {
        return tiltTraySorter;
    }

    public void setTiltTraySorter(double tiltTraySorter) {
        this.tiltTraySorter = tiltTraySorter;
    }

    public double getPalletConveyor() {
        return palletConveyor;
    }

    public void setPalletConveyor(double palletConveyor) {
        this.palletConveyor = palletConveyor;
    }

    public double getTotalInvestment() {
        return this.nonPoweredRoller * InputWeightEnum.NonPoweredRoller.getWeight() +
                this.poweredRollerConveyor * InputWeightEnum.PoweredRollerConveyor.getWeight() +
                this.powerBelt * InputWeightEnum.PowerBelt.getWeight() +
                this.skateWheel * InputWeightEnum.SkateWheel.getWeight() +
                this.towLine * InputWeightEnum.TowLine.getWeight() +
                this.tiltTraySorter * InputWeightEnum.TiltTraySorter.getWeight() +
                this.palletConveyor * InputWeightEnum.PalletConveyor.getWeight();
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.serialize(this);
    }
}
