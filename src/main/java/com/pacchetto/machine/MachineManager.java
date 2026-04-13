package com.pacchetto.machine;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.db.DatabaseManager;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.items.ItemRegistry;
import com.pacchetto.recipe.RecipeRegistry;
import com.pacchetto.util.LocationKey;
import com.pacchetto.util.TickBatcher;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MachineManager {
    private final ServerMCPlugin plugin;
    private final DatabaseManager databaseManager;
    private final EnergyManager energyManager;
    private final Map<LocationKey, MachineState> machines = new ConcurrentHashMap<>();
    private final Map<MachineType, MachineLogic> logics = new EnumMap<>(MachineType.class);
    private final TickBatcher<MachineState> batcher = new TickBatcher<>();
    private int taskId = -1;

    public MachineManager(ServerMCPlugin plugin, DatabaseManager databaseManager, EnergyManager energyManager, RecipeRegistry recipeRegistry) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.energyManager = energyManager;
        ItemRegistry itemRegistry = plugin.getItemRegistry();
        logics.put(MachineType.GENERATOR, new GeneratorMachine(energyManager));
        logics.put(MachineType.PROCESSOR, new ProcessorMachine(recipeRegistry, itemRegistry));
        logics.put(MachineType.ADVANCED_FURNACE, new AdvancedFurnaceMachine());
    }

    public void place(Location loc, MachineType type) {
        LocationKey key = LocationKey.of(loc);
        MachineState state = new MachineState(key, type, 1);
        machines.put(key, state);
        energyManager.createNode(key);
    }

    public MachineState get(Location loc) {
        return machines.get(LocationKey.of(loc));
    }

    public Collection<MachineState> getAllMachines() {
        return machines.values();
    }

    public MachineState getByKey(LocationKey key) {
        return machines.get(key);
    }

    public void breakMachine(Location loc) {
        LocationKey key = LocationKey.of(loc);
        machines.remove(key);
        energyManager.removeNode(key);
        databaseManager.deleteMachine(key);
    }

    public void startTicker() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, () -> {
            batcher.refresh(new ArrayList<>(machines.values()));
            int batchSize = Math.max(1, plugin.getConfig().getInt("performance.batch-size", 30));
            for (MachineState machine : batcher.nextBatch(batchSize)) {
                int imported = energyManager.pull(machine.key(), 100);
                machine.addEnergy(imported);
                MachineLogic logic = logics.get(machine.type());
                if (logic != null) logic.tick(machine);
            }
        }, 1L, 1L);
    }

    public void stopTicker() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    public void saveMachines() {
        for (MachineState state : machines.values()) {
            databaseManager.upsertMachine(state);
        }
    }

    public void loadMachines() {
        List<MachineState> loaded = databaseManager.loadMachines();
        for (MachineState state : loaded) {
            machines.put(state.key(), state);
            energyManager.createNode(state.key());
        }
    }
}
