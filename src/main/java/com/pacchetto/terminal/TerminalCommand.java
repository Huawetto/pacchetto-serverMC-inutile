package com.pacchetto.terminal;

import com.pacchetto.ai.AIOrchestrator;
import com.pacchetto.cargo.CargoManager;
import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.machine.MachineManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class TerminalCommand implements CommandExecutor, TabCompleter {
    private final ServerMCPlugin plugin;
    private final MachineManager machineManager;
    private final EnergyManager energyManager;
    private final CargoManager cargoManager;
    private final AIOrchestrator ai;

    public TerminalCommand(ServerMCPlugin plugin, MachineManager machineManager, EnergyManager energyManager, CargoManager cargoManager, AIOrchestrator ai) {
        this.plugin = plugin;
        this.machineManager = machineManager;
        this.energyManager = energyManager;
        this.cargoManager = cargoManager;
        this.ai = ai;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "/fun <guide|energy|machines|cargo|debug|ai|help>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "guide" -> openGuide(sender);
            case "energy" -> sender.sendMessage(ChatColor.GREEN + "Total network energy: " + energyManager.totalStored());
            case "machines" -> sender.sendMessage(ChatColor.YELLOW + "Active machines: " + machineManager.getAllMachines().size());
            case "cargo" -> sender.sendMessage(ChatColor.GOLD + "Cargo channels: " + cargoManager.channelCount());
            case "debug" -> sender.sendMessage(ChatColor.RED + "Heap machines snapshot: " + machineManager.getAllMachines().size());
            case "ai" -> sender.sendMessage(ChatColor.LIGHT_PURPLE + ai.diagnostics());
            case "help" -> sender.sendMessage(ChatColor.AQUA + "Comandi: /fun guide, /fun energy, /fun machines, /fun cargo, /fun ai, /fun debug");
            default -> sender.sendMessage(ChatColor.RED + "Comando sconosciuto. Usa /fun help");
        }
        return true;
    }

    private void openGuide(CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(ChatColor.RED + "Solo i player possono aprire la guida GUI.");
            return;
        }
        Inventory guide = Bukkit.createInventory(player, 27, ChatColor.DARK_AQUA + "FUN Guide");
        guide.setItem(10, withLore(plugin.getItemRegistry().get("machine_generator"), "Generatore", "Produce energia continuamente"));
        guide.setItem(12, withLore(plugin.getItemRegistry().get("machine_processor"), "Processor", "Esegue ricette custom multi-input"));
        guide.setItem(14, withLore(plugin.getItemRegistry().get("machine_furnace"), "Advanced Furnace", "Fonde risorse vanilla con energia"));
        guide.setItem(16, withLore(plugin.getItemRegistry().get("terminal_chip"), "Terminale", "Usa /fun ai, /fun energy, /fun cargo"));
        guide.setItem(22, withLore(new ItemStack(Material.BOOK), "Comandi FUN", "/fun guide", "/fun machines", "/fun energy", "/fun cargo", "/fun ai", "/fun debug"));
        player.openInventory(guide);
    }

    private ItemStack withLore(ItemStack base, String name, String... loreLines) {
        ItemStack stack = base == null ? new ItemStack(Material.BARRIER) : base.clone();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA + name);
        meta.setLore(List.of(loreLines).stream().map(s -> ChatColor.GRAY + s).toList());
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("guide", "energy", "machines", "cargo", "ai", "debug", "help");
        }
        return List.of();
    }
}
