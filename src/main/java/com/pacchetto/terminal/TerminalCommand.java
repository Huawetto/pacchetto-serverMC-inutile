package com.pacchetto.terminal;

import com.pacchetto.ai.AIOrchestrator;
import com.pacchetto.cargo.CargoManager;
import com.pacchetto.energy.EnergyManager;
import com.pacchetto.machine.MachineManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.List;

public class TerminalCommand implements CommandExecutor, TabCompleter {
    private final MachineManager machineManager;
    private final EnergyManager energyManager;
    private final CargoManager cargoManager;
    private final AIOrchestrator ai;

    public TerminalCommand(Object plugin, MachineManager machineManager, EnergyManager energyManager, CargoManager cargoManager, AIOrchestrator ai) {
        this.machineManager = machineManager;
        this.energyManager = energyManager;
        this.cargoManager = cargoManager;
        this.ai = ai;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sender.sendMessage(ChatColor.AQUA + "/terminal <energy|machines|cargo|debug|ai>");
            return true;
        }
        switch (args[0].toLowerCase()) {
            case "energy" -> sender.sendMessage(ChatColor.GREEN + "Total network energy: " + energyManager.totalStored());
            case "machines" -> sender.sendMessage(ChatColor.YELLOW + "Active machines: " + machineManager.getAllMachines().size());
            case "cargo" -> sender.sendMessage(ChatColor.GOLD + "Cargo channels: " + cargoManager.channelCount());
            case "debug" -> sender.sendMessage(ChatColor.RED + "Heap machines snapshot: " + machineManager.getAllMachines());
            case "ai" -> sender.sendMessage(ChatColor.LIGHT_PURPLE + ai.diagnostics());
            default -> sender.sendMessage(ChatColor.RED + "Unknown module.");
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return List.of("energy", "machines", "cargo", "debug", "ai");
        }
        return List.of();
    }
}
