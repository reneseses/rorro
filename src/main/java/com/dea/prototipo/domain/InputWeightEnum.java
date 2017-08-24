package com.dea.prototipo.domain;

//de nuevoo
public enum InputWeightEnum {
    // Vehicles
    PalletTruck(7000000),
    WalkieStacker(5600000),
    SitDownCounterBalance(21000000),
    StandUpCounterBalance(21000000),
    StraddleTruck(24500000),
    StraddleReachTruck(31500000),
    SideLoaderTruck(45500000),
    TurretTruck(56000000),
    HybridTruck(70000000),
    PalletASRSMachine(105000000),
    RailGuidedOrderPicker(21000000),
    WireGuidedOrderPicker(24500000),
    AGV(56000000),

    // Storage
    PersonAboardASRSAisles(77000000),
    HorizontalCarousel(28000000),
    VerticalCarousel(45500000),
    MiniloadASRSAisles(87500000),
    AFrameDispenser(525000),

    // Conveyor
    NonPoweredRoller(10500),
    PoweredRollerConveyor(42000),
    PowerBelt(21000),
    SkateWheel(5250),
    TowLine(21000),
    TiltTraySorter(210000),
    PalletConveyor(210000);

    private int weight;

    InputWeightEnum(int weight) {
        this.weight = weight;
    }

    public int getWeight() {
        return this.weight;
    }
}
