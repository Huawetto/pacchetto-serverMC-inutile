package com.pacchetto.cargo;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.machine.MachineManager;
import com.pacchetto.machine.MachineState;
import com.pacchetto.machine.MachineType;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class CargoManager {
    private final ServerMCPlugin plugin;
    private final MachineManager machineManager;
    private int taskId = -1;
    private final List<CargoChannel> channels = new ArrayList<>();

    public CargoManager(ServerMCPlugin plugin, MachineManager machineManager) {
        this.plugin = plugin;
        this.machineManager = machineManager;
        for (int i = 0; i < 20; i++) {
            channels.add(new CargoChannel(i, 20 - i, null));
        }
    }

    public void start() {
        taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this::tick, 10L, 10L);
    }

    public void stop() {
        if (taskId != -1) Bukkit.getScheduler().cancelTask(taskId);
    }

    private void tick() {
        List<MachineState> outputs = machineManager.getAllMachines().stream()
                .filter(m -> m.type() == MachineType.PROCESSOR || m.type() == MachineType.ADVANCED_FURNACE)
                .toList();
        List<MachineState> inputs = machineManager.getAllMachines().stream()
                .filter(m -> m.type() == MachineType.PROCESSOR)
                .toList();

        channels.stream().sorted(Comparator.comparingInt(CargoChannel::priority).reversed()).forEach(channel -> {
            for (MachineState from : outputs) {
                ItemStack candidate = findTransferable(from);
                if (candidate == null) continue;
                if (channel.filterItemId() != null && !channel.filterItemId().equals(plugin.getItemRegistry().id(candidate))) continue;
                for (MachineState to : inputs) {
                    if (to == from) continue;
                    if (transferOne(from, to, candidate)) return;
                }
            }
        });
    }

    private ItemStack findTransferable(MachineState from) {
        for (int i = 0; i < from.inventory().getSize(); i++) {
            ItemStack stack = from.inventory().getItem(i);
            if (stack == null || stack.getAmount() <= 0) continue;
            return stack;
        }
        return null;
    }

    private boolean transferOne(MachineState from, MachineState to, ItemStack stack) {
        ItemStack one = stack.clone();
        one.setAmount(1);
        if (!to.inventory().addItem(one).isEmpty()) return false;
        stack.setAmount(stack.getAmount() - 1);
        return true;
    }

    public int channelCount() {
        return channels.size();
    }
}
