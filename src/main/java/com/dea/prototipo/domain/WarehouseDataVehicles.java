package com.dea.prototipo.domain;

import flexjson.JSONSerializer;
import org.springframework.beans.factory.annotation.Configurable;

import javax.persistence.*;
import javax.validation.constraints.Min;

@Configurable
@Entity
public class WarehouseDataVehicles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    /**
     * Carretillas
     */
    @Min(0L)
    private int palletTruck = 0;

    /**
     * Carretillas Apiladoras
     */
    @Min(0L)
    private int walkieStacker = 0;

    /**
     * Carretillas Contrabalanceadas hombre sentado
     */
    @Min(0L)
    private int sitDownCounterBalance = 0;

    /**
     * Carretillas Cotrabalanceadas hombre parado
     */
    @Min(0L)
    private int standUpCounterBalance = 0;

    /**
     * Carretillas Retráctiles
     */
    @Min(0L)
    private int straddleTruck = 0;

    /**
     * Carretillas Retráctiles Pantógrafo
     */
    @Min(0L)
    private int straddleReachTruck = 0;

    /**
     * Cargadores Laterales
     */
    @Min(0L)
    private int sideLoaderTruck = 0;

    /**
     * Carretillas Trilaterales
     */
    @Min(0L)
    private int turretTruck = 0;

    /**
     * Transelevador hombre a bordo (Transtacker)
     */
    @Min(0L)
    private int hybridTruck = 0;

    /**
     * Transelevadores (ASRS)
     */
    @Min(0L)
    private int palletASRSMachine = 0;

    /**
     * Carretillas Preparadoras de Pedido guiado por riel
     */
    @Min(0L)
    private int railGuidedOrderPicker = 0;

    /**
     * Carretillas Preparadoras de Pedido giado por cable
     */
    @Min(0L)
    private int wireGuidedOrderPicker = 0;

    /**
     * Vehículos autoguiados (AVG)
     */
    @Min(0L)
    private int agv = 0;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getPalletTruck() {
        return palletTruck;
    }

    public void setPalletTruck(int palletTruck) {
        this.palletTruck = palletTruck;
    }

    public int getWalkieStacker() {
        return walkieStacker;
    }

    public void setWalkieStacker(int walkieStacker) {
        this.walkieStacker = walkieStacker;
    }

    public int getSitDownCounterBalance() {
        return sitDownCounterBalance;
    }

    public void setSitDownCounterBalance(int sitDownCounterBalance) {
        this.sitDownCounterBalance = sitDownCounterBalance;
    }

    public int getStandUpCounterBalance() {
        return standUpCounterBalance;
    }

    public void setStandUpCounterBalance(int standUpCounterBalance) {
        this.standUpCounterBalance = standUpCounterBalance;
    }

    public int getStraddleTruck() {
        return straddleTruck;
    }

    public void setStraddleTruck(int straddleTruck) {
        this.straddleTruck = straddleTruck;
    }

    public int getStraddleReachTruck() {
        return straddleReachTruck;
    }

    public void setStraddleReachTruck(int straddleReachTruck) {
        this.straddleReachTruck = straddleReachTruck;
    }

    public int getSideLoaderTruck() {
        return sideLoaderTruck;
    }

    public void setSideLoaderTruck(int sideLoaderTruck) {
        this.sideLoaderTruck = sideLoaderTruck;
    }

    public int getTurretTruck() {
        return turretTruck;
    }

    public void setTurretTruck(int turretTruck) {
        this.turretTruck = turretTruck;
    }

    public int getHybridTruck() {
        return hybridTruck;
    }

    public void setHybridTruck(int hybridTruck) {
        this.hybridTruck = hybridTruck;
    }

    public int getPalletASRSMachine() {
        return palletASRSMachine;
    }

    public void setPalletASRSMachine(int palletASRSMachine) {
        this.palletASRSMachine = palletASRSMachine;
    }

    public int getRailGuidedOrderPicker() {
        return railGuidedOrderPicker;
    }

    public void setRailGuidedOrderPicker(int railGuidedOrderPicker) {
        this.railGuidedOrderPicker = railGuidedOrderPicker;
    }

    public int getWireGuidedOrderPicker() {
        return wireGuidedOrderPicker;
    }

    public void setWireGuidedOrderPicker(int wireGuidedOrderPicker) {
        this.wireGuidedOrderPicker = wireGuidedOrderPicker;
    }

    public int getAgv() {
        return agv;
    }

    public void setAgv(int agv) {
        this.agv = agv;
    }

    public int getTotalInvestment() {
        return this.palletTruck * InputWeightEnum.PalletTruck.getWeight() +
                this.walkieStacker * InputWeightEnum.WalkieStacker.getWeight() +
                this.sitDownCounterBalance * InputWeightEnum.SitDownCounterBalance.getWeight() +
                this.standUpCounterBalance * InputWeightEnum.StandUpCounterBalance.getWeight() +
                this.straddleTruck * InputWeightEnum.StraddleTruck.getWeight() +
                this.straddleReachTruck * InputWeightEnum.StraddleReachTruck.getWeight() +
                this.sideLoaderTruck * InputWeightEnum.SideLoaderTruck.getWeight() +
                this.turretTruck * InputWeightEnum.TurretTruck.getWeight() +
                this.hybridTruck * InputWeightEnum.HybridTruck.getWeight() +
                this.palletASRSMachine * InputWeightEnum.PalletASRSMachine.getWeight() +
                this.railGuidedOrderPicker * InputWeightEnum.RailGuidedOrderPicker.getWeight() +
                this.wireGuidedOrderPicker * InputWeightEnum.WireGuidedOrderPicker.getWeight() +
                this.agv * InputWeightEnum.AGV.getWeight();
    }

    public String toString() {
        JSONSerializer serializer = new JSONSerializer();
        return serializer.serialize(this);
    }
}
