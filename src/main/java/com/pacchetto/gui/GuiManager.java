package com.pacchetto.gui;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.machine.MachineManager;
import com.pacchetto.machine.MachineState;
import com.pacchetto.machine.MachineType;
import com.pacchetto.util.LocationKey;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class GuiManager implements Listener {
    private final Map<Player, LocationKey> openMachine = new HashMap<>();
    private final MachineManager machineManager;

    public GuiManager(ServerMCPlugin plugin, MachineManager machineManager, EnergyManager energyManager) {
        this.machineManager = machineManager;
        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Map.Entry<Player, LocationKey> entry : openMachine.entrySet()) {
                Player player = entry.getKey();
                MachineState machine = machineManager.getByKey(entry.getValue());
                if (machine == null) continue;
                updateStatus(player.getOpenInventory().getTopInventory(), machine);
            }
        }, 5L, 5L);
    }

    public void openMachineGui(Player player, MachineState machine) {
        Inventory inv = Bukkit.createInventory(player, 54, ChatColor.DARK_AQUA + machine.type().name() + " • L" + machine.level());
        renderMachineLayout(inv, machine.type());
        updateStatus(inv, machine);
        player.openInventory(inv);
        openMachine.put(player, machine.key());
    }

    private void renderMachineLayout(Inventory inv, MachineType type) {
        for (int i = 0; i < 54; i++) {
            if (i < 9 || i > 44 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, pane(Material.GRAY_STAINED_GLASS_PANE, " "));
            }
        }
        switch (type) {
            case GENERATOR -> {
                inv.setItem(20, pane(Material.REDSTONE_BLOCK, ChatColor.RED + "Fuel/Input"));
                inv.setItem(24, pane(Material.LIGHTNING_ROD, ChatColor.YELLOW + "Energy Output"));
            }
            case PROCESSOR -> {
                inv.setItem(19, pane(Material.CHEST, ChatColor.AQUA + "Input A"));
                inv.setItem(20, pane(Material.CHEST, ChatColor.AQUA + "Input B"));
                inv.setItem(21, pane(Material.CHEST, ChatColor.AQUA + "Input C"));
                inv.setItem(23, pane(Material.HOPPER, ChatColor.GREEN + "Output"));
            }
            case ADVANCED_FURNACE -> {
                inv.setItem(20, pane(Material.IRON_ORE, ChatColor.GOLD + "Smelt Input"));
                inv.setItem(24, pane(Material.IRON_INGOT, ChatColor.GREEN + "Smelt Output"));
            }
        }
        inv.setItem(49, pane(Material.NETHER_STAR, ChatColor.GREEN + "Status"));
    }

    private void updateStatus(Inventory inv, MachineState machine) {
        int bars = Math.min(9, machine.progress() / 10);
        for (int i = 45; i < 54; i++) {
            inv.setItem(i, pane(i - 45 < bars ? Material.LIME_STAINED_GLASS_PANE : Material.RED_STAINED_GLASS_PANE,
                    ChatColor.AQUA + "P:" + machine.progress() + " E:" + machine.energyBuffer()));
        }
    }

    private ItemStack pane(Material material, String name) {
        ItemStack stack = new ItemStack(material);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(name);
        stack.setItemMeta(meta);
        return stack;
    }

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        HumanEntity who = event.getWhoClicked();
        if (!(who instanceof Player player)) return;
        if (!openMachine.containsKey(player)) return;
        if (event.getRawSlot() < event.getView().getTopInventory().getSize()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getPlayer() instanceof Player player) {
            openMachine.remove(player);
        }
    }
}
