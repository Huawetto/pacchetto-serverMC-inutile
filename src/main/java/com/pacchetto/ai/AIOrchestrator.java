package com.pacchetto.ai;

import com.pacchetto.cargo.CargoManager;
import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.machine.MachineManager;
import com.pacchetto.machine.MachineState;
import com.pacchetto.machine.MachineType;
import org.bukkit.Bukkit;

import java.util.Comparator;

public class AIOrchestrator {
    private final ServerMCPlugin plugin;
    private final MachineManager machineManager;
    private final CargoManager cargoManager;
    private final EnergyManager energyManager;
    private int taskId = -1;

    public AIOrchestrator(ServerMCPlugin plugin, MachineManager machineManager, CargoManager cargoManager, EnergyManager energyManager) {
        this.plugin = plugin;
        this.machineManager = machineManager;
        this.cargoManager = cargoManager;
        this.energyManager = energyManager;
    }

    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::runOptimization, 100L, 100L);
    }

    public void stop() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    private void runOptimization() {
        MachineState hottest = machineManager.getAllMachines().stream()
                .filter(m -> m.type() == MachineType.PROCESSOR || m.type() == MachineType.ADVANCED_FURNACE)
                .max(Comparator.comparingInt(MachineState::progress))
                .orElse(null);
        if (hottest != null) {
            int bonus = Math.min(80, energyManager.pull(hottest.key(), 80));
            hottest.addEnergy(bonus);
        }
    }

    public String diagnostics() {
        return "ai:machines=" + machineManager.getAllMachines().size() + ",cargoChannels=" + cargoManager.channelCount() + ",energy=" + energyManager.totalStored();
    }
}
