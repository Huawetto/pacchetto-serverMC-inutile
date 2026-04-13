package com.pacchetto.core;

import com.pacchetto.items.ItemRegistry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class AntiDupeGuard {
    private final ItemRegistry itemRegistry;

    public AntiDupeGuard(ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public boolean validateInventory(Inventory inv) {
        for (ItemStack item : inv.getContents()) {
            if (item == null) continue;
            if (item.getType().name().contains("PLAYER_HEAD") && !itemRegistry.isCustom(item)) {
                return false;
            }
            if (item.getAmount() > item.getMaxStackSize()) {
                return false;
            }
        }
        return true;
    }
}
