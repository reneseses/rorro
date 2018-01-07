package com.dea.prototipo.domain;

public enum ProductType {
    Forest("Forestal"),
    Paper("Papel"),
    IndustrialMetalsAndMining("Metales industriales y minería"),
    Mining("Minería"),
    ChemicalProducts("Productos químicos"),
    ConstructionAndMaterials("Construcción y materiales"),
    ElectricalAndElectronicComponentsAndEquipment("Componentes y equipamiento eléctrico y electrónico"),
    IndustrialMachinery("Maquinaria industrial"),
    AutomotivePartsAndAccessories("Partes automotrices y accesorios"),
    Drinks("Bebidas"),
    Foods("Alimentos"),
    DurableHouseholdProducts("Productos domésticos durables"),
    NonDurableHouseholdProducts("Productos domésticos no durables"),
    Furniture("Muebles"),
    ConsumerElectronics("Electrónica de consumo"),
    Toys("Juguetes"),
    ClothingFootwearAndAccessories("Ropa, calzado y accesorios"),
    PersonalCareProducts("Productos cuidado personal"),
    Tobacco("Tabaco"),
    MedicalEquipmentAndSupplies("Equipos y suministros médicos"),
    Editorial("Editorial"),
    Pharmacies("Farmacias"),
    Supermarkets("Supermercados"),
    DepartmentStores("Tiendas departamentales"),
    RetailHomeImprovement("Minoristas mejoramiento del hogar"),
    Semiconductors("Semiconductores"),
    TelecommunicationsEquipment("Equipos de telecomunicaciones");

    private String label;

    ProductType(String label) {
        this.label = label;
    }

    public String getName() {
        return this.label;
    }
}
