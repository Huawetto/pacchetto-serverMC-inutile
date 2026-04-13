package com.pacchetto.core;

import com.pacchetto.ai.AIOrchestrator;
import com.pacchetto.cargo.CargoManager;
import com.pacchetto.db.DatabaseManager;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.gui.GuiManager;
import com.pacchetto.items.ItemRegistry;
import com.pacchetto.machine.MachineListener;
import com.pacchetto.machine.MachineManager;
import com.pacchetto.recipe.RecipeRegistry;
import com.pacchetto.terminal.TerminalCommand;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class ServerMCPlugin extends JavaPlugin {

    private DatabaseManager databaseManager;
    private EnergyManager energyManager;
    private ItemRegistry itemRegistry;
    private RecipeRegistry recipeRegistry;
    private MachineManager machineManager;
    private CargoManager cargoManager;
    private GuiManager guiManager;
    private AIOrchestrator aiOrchestrator;

    @Override
    public void onEnable() {
        bootstrapDataFiles();
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.init();

        this.energyManager = new EnergyManager(this);
        this.itemRegistry = new ItemRegistry(this);
        this.recipeRegistry = new RecipeRegistry(this, itemRegistry);
        this.machineManager = new MachineManager(this, databaseManager, energyManager, recipeRegistry);
        this.cargoManager = new CargoManager(this, machineManager);
        this.guiManager = new GuiManager(this, machineManager, energyManager);
        this.aiOrchestrator = new AIOrchestrator(this, machineManager, cargoManager, energyManager);

        this.itemRegistry.register();
        this.recipeRegistry.registerDefaults();
        this.machineManager.loadMachines();

        Bukkit.getPluginManager().registerEvents(new MachineListener(this, machineManager, itemRegistry, guiManager), this);
        Bukkit.getPluginManager().registerEvents(guiManager, this);

        TerminalCommand terminalCommand = new TerminalCommand(this, machineManager, energyManager, cargoManager, aiOrchestrator);
        getCommand("terminal").setExecutor(terminalCommand);
        getCommand("terminal").setTabCompleter(terminalCommand);

        machineManager.startTicker();
        cargoManager.start();
        aiOrchestrator.start();

        getLogger().info("ServerMCInutile enabled with " + machineManager.getAllMachines().size() + " loaded machines.");
    }

    private void bootstrapDataFiles() {
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            getLogger().warning("Cannot create plugin data folder: " + getDataFolder().getAbsolutePath());
        }
        saveResourceIfMissing("config.yml");
    }

    private void saveResourceIfMissing(String fileName) {
        java.io.File target = new java.io.File(getDataFolder(), fileName);
        if (!target.exists()) {
            saveResource(fileName, false);
        }
    }

    @Override
    public void onDisable() {
        if (aiOrchestrator != null) aiOrchestrator.stop();
        if (cargoManager != null) cargoManager.stop();
        if (machineManager != null) {
            machineManager.saveMachines();
            machineManager.stopTicker();
        }
        if (databaseManager != null) databaseManager.close();
    }

    public ItemRegistry getItemRegistry() {
        return itemRegistry;
    }
}
