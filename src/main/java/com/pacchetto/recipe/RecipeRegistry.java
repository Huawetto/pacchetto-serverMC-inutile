package com.pacchetto.recipe;

import com.pacchetto.core.ServerMCPlugin;
import com.pacchetto.items.ItemRegistry;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class RecipeRegistry {
    private final ItemRegistry itemRegistry;
    private final Map<String, MachineRecipe> recipes = new HashMap<>();

    public RecipeRegistry(ServerMCPlugin plugin, ItemRegistry itemRegistry) {
        this.itemRegistry = itemRegistry;
    }

    public void registerDefaults() {
        register(new MachineRecipe("cpu_from_silicon", List.of("ore_silicon", "ore_copper", "pcb"), "part_cpu", 120, 8));
        register(new MachineRecipe("ram_from_silicon", List.of("ore_silicon", "part_bus"), "part_ram", 100, 6));
        register(new MachineRecipe("energy_cell_assembly", List.of("ore_lithium", "energy_coil", "energy_capacitor"), "energy_cell", 180, 10));
        register(new MachineRecipe("ai_core_build", List.of("part_cpu", "part_ram", "module_net", "telemetry"), "module_ai", 220, 14));
        register(new MachineRecipe("quantum_matrix", List.of("quantum_dust", "module_ai", "part_ssd"), "module_quantum", 260, 16));
    }

    public void register(MachineRecipe recipe) {
        recipes.put(recipe.id(), recipe);
    }

    public Optional<MachineRecipe> findMatching(Inventory inputInventory) {
        Map<String, Integer> available = countItems(inputInventory);
        return recipes.values().stream().filter(recipe -> matches(available, recipe)).findFirst();
    }

    public void consumeIngredients(Inventory inputInventory, MachineRecipe recipe) {
        List<String> needed = new ArrayList<>(recipe.inputIds());
        for (int slot = 0; slot < inputInventory.getSize(); slot++) {
            ItemStack stack = inputInventory.getItem(slot);
            if (stack == null || stack.getAmount() <= 0) continue;
            String id = itemRegistry.id(stack);
            if (id == null) continue;
            if (needed.remove(id)) {
                stack.setAmount(stack.getAmount() - 1);
                if (stack.getAmount() <= 0) {
                    inputInventory.setItem(slot, null);
                } else {
                    inputInventory.setItem(slot, stack);
                }
            }
            if (needed.isEmpty()) break;
        }
    }

    private boolean matches(Map<String, Integer> available, MachineRecipe recipe) {
        Map<String, Integer> needed = new HashMap<>();
        for (String id : recipe.inputIds()) {
            needed.merge(id, 1, Integer::sum);
        }
        for (Map.Entry<String, Integer> entry : needed.entrySet()) {
            if (available.getOrDefault(entry.getKey(), 0) < entry.getValue()) {
                return false;
            }
        }
        return true;
    }

    public Optional<MachineRecipe> findByOutput(String outputId) {
        return recipes.values().stream().filter(r -> r.outputId().equals(outputId)).findFirst();
    }

    private Map<String, Integer> countItems(Inventory inventory) {
        Map<String, Integer> count = new HashMap<>();
        for (ItemStack stack : inventory.getContents()) {
            if (stack == null) continue;
            String id = itemRegistry.id(stack);
            if (id == null) continue;
            count.merge(id, stack.getAmount(), Integer::sum);
        }
        return count;
    }
}
