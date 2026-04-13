package com.pacchetto.items;

import com.pacchetto.core.ServerMCPlugin;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRegistry {
    private final ItemFactory factory;
    private final Map<String, CustomItem> items = new HashMap<>();

    public ItemRegistry(ServerMCPlugin plugin) {
        this.factory = new ItemFactory(plugin);
    }

    public void register() {
        register("machine_generator", "Generator Node", List.of("Power module", "Load-aware output", "I/O: 2A"));
        register("machine_processor", "Logic Processor", List.of("Recipe executor", "4 parallel slots", "Bus v1"));
        register("machine_furnace", "Advanced Furnace", List.of("Thermal compute", "Smelt acceleration", "Heat sink"));
        register("part_cpu", "Nano CPU", List.of("8-thread microcore", "2.7GHz equivalent", "ARM-like ISA"));
        register("part_ram", "Crystal RAM", List.of("ECC storage", "16KB block", "Low-latency"));
        register("part_ssd", "Quantum SSD", List.of("Persistent matrix", "Wear-leveling", "512 sectors"));
        register("part_gpu", "Render GPU", List.of("GPGPU kernels", "Image DSP", "Parallel lanes"));
        register("part_bus", "Data Bus", List.of("64-bit lane", "Backplane bridge", "Noise shield"));
        register("part_fan", "Active Cooler", List.of("Rotor v3", "Thermal pad", "PWM control"));
        register("part_case", "Containment Case", List.of("EMI frame", "Shock mount", "Composite alloy"));
        register("energy_cell", "Energy Cell", List.of("Store: 5kFE", "High discharge", "Li-graphene"));
        register("energy_coil", "Flux Coil", List.of("Inductive stage", "Pulse transform", "Copper weave"));
        register("energy_capacitor", "Hyper Capacitor", List.of("Burst energy", "Fast recharge", "ESR low"));
        register("ore_silicon", "Silicon Wafer", List.of("Photolithography", "0.7nm node", "Polished"));
        register("ore_copper", "Refined Copper", List.of("Conductive grade", "Cargo compatible", "Clean plate"));
        register("ore_lithium", "Lithium Pellet", List.of("Battery chemistry", "Dry storage", "Stable"));
        register("module_ai", "AI Core", List.of("Routing optimizer", "Predictive load", "Model v2"));
        register("module_io", "I/O Controller", List.of("Slot arbitration", "DMA bridge", "Parity checks"));
        register("module_net", "Network Adapter", List.of("Packet switch", "Channel map", "QoS flags"));
        register("module_quantum", "Quantum Matrix", List.of("Entangled lane", "Noise correction", "Q-state"));
        register("cargo_input", "Cargo Input", List.of("Ingress node", "20 channels", "Filter aware"));
        register("cargo_output", "Cargo Output", List.of("Egress node", "Priority route", "Buffered"));
        register("cargo_connector", "Cargo Connector", List.of("Bridge cable", "Hop routing", "Latency 1"));
        register("terminal_chip", "Terminal Chip", List.of("CLI parser", "Auth token", "Debug uplink"));
        register("debug_probe", "Debug Probe", List.of("Trace bus", "Signal inspect", "Safe mode"));
        register("quantum_dust", "Quantum Dust", List.of("High entropy", "Recipe catalyst", "Reactive"));
        register("logic_gate", "Logic Gate", List.of("Boolean matrix", "TTL bridge", "Signal lock"));
        register("pcb", "Printed Board", List.of("4-layer PCB", "Via stitched", "Solder mask"));
        register("coolant", "Cryo Coolant", List.of("Sub-zero loop", "Evap control", "Stable mix"));
        register("telemetry", "Telemetry Core", List.of("Metric stream", "Anomaly detect", "Ring buffer"));
    }

    private void register(String id, String display, List<String> lore) {
        String tex = "eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUv" + Integer.toHexString(id.hashCode()).replace('-', 'a') + ""; 
        ItemStack stack = factory.createTechHead(id, display, lore, tex + "YmI1ZmM1YmY2ZTgwOTM4YWJmNDQ2NGVjZmI4ZDJhYTg0N2Q5N2E2In19fQ==");
        items.put(id, new CustomItem(id, stack));
    }

    public ItemStack get(String id) {
        CustomItem item = items.get(id);
        return item == null ? null : item.stack().clone();
    }

    public boolean isCustom(ItemStack stack) {
        return factory.isCustom(stack);
    }

    public String id(ItemStack stack) {
        return factory.id(stack);
    }

    public Collection<CustomItem> allItems() {
        return items.values();
    }
}
