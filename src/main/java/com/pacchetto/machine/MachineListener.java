package com.pacchetto.machine;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.gui.GuiManager;
import com.pacchetto.items.ItemRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class MachineListener implements Listener {
    private final MachineManager machineManager;
    private final ItemRegistry itemRegistry;
    private final GuiManager guiManager;

    public MachineListener(ServerMCPlugin plugin, MachineManager machineManager, ItemRegistry itemRegistry, GuiManager guiManager) {
        this.machineManager = machineManager;
        this.itemRegistry = itemRegistry;
        this.guiManager = guiManager;
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent event) {
        ItemStack hand = event.getItemInHand();
        String id = itemRegistry.id(hand);
        if (id == null) return;
        MachineType type = switch (id) {
            case "machine_generator" -> MachineType.GENERATOR;
            case "machine_processor" -> MachineType.PROCESSOR;
            case "machine_furnace" -> MachineType.ADVANCED_FURNACE;
            default -> null;
        };
        if (type == null) {
            event.setCancelled(true);
            return;
        }
        machineManager.place(event.getBlockPlaced().getLocation(), type);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        MachineState state = machineManager.get(block.getLocation());
        if (state == null) return;
        block.setType(Material.AIR);
        event.setDropItems(false);
        machineManager.breakMachine(block.getLocation());
        event.getPlayer().sendMessage(ChatColor.YELLOW + "Machine removed: " + state.type());
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getHand() != EquipmentSlot.HAND || event.getClickedBlock() == null) return;
        MachineState state = machineManager.get(event.getClickedBlock().getLocation());
        if (state == null) return;
        event.setCancelled(true);
        Player player = event.getPlayer();
        guiManager.openMachineGui(player, state);
    }
}
