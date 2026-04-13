package com.pacchetto.machine;

import com.pacchetto.util.LocationKey;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;

public class MachineState {
    private final LocationKey key;
    private final MachineType type;
    private final Inventory inventory;
    private int level;
    private int progress;
    private int energyBuffer;
    private String activeRecipe;

    public MachineState(LocationKey key, MachineType type, int level) {
        this.key = key;
        this.type = type;
        this.level = level;
        this.inventory = Bukkit.createInventory(null, 27, "[" + type.name() + "]");
    }

    public LocationKey key() { return key; }
    public MachineType type() { return type; }
    public Inventory inventory() { return inventory; }
    public int level() { return level; }
    public void setLevel(int level) { this.level = Math.max(1, Math.min(10, level)); }
    public int progress() { return progress; }
    public void setProgress(int progress) { this.progress = Math.max(0, progress); }
    public int energyBuffer() { return energyBuffer; }
    public void addEnergy(int amount) { energyBuffer = Math.min(50000, energyBuffer + Math.max(0, amount)); }
    public boolean consumeEnergy(int amount) {
        if (energyBuffer < amount) return false;
        energyBuffer -= amount;
        return true;
    }
    public String activeRecipe() { return activeRecipe; }
    public void setActiveRecipe(String activeRecipe) { this.activeRecipe = activeRecipe; }
}
