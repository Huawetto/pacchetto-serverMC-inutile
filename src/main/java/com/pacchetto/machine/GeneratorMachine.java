package com.pacchetto.machine;

import com.pacchetto.energy.EnergyManager;

public class GeneratorMachine implements MachineLogic {
    private final EnergyManager energyManager;

    public GeneratorMachine(EnergyManager energyManager) {
        this.energyManager = energyManager;
    }

    @Override
    public void tick(MachineState state) {
        int generation = 20 + (state.level() * 5);
        state.addEnergy(generation);
        energyManager.inject(state.key(), generation);
    }
}
