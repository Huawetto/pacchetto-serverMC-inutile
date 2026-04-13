package com.pacchetto.tech;

public enum TechTier {
    SILICON(1.0, 1.0),
    NANO(1.2, 0.9),
    QUANTUM(1.5, 0.75);

    private final double speedMultiplier;
    private final double energyMultiplier;

    TechTier(double speedMultiplier, double energyMultiplier) {
        this.speedMultiplier = speedMultiplier;
        this.energyMultiplier = energyMultiplier;
    }

    public double speedMultiplier() { return speedMultiplier; }
    public double energyMultiplier() { return energyMultiplier; }
}
