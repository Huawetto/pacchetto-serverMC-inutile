package com.pacchetto.machine;

import com.pacchetto.items.ItemRegistry;
import com.pacchetto.recipe.MachineRecipe;
import com.pacchetto.recipe.RecipeRegistry;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ProcessorMachine implements MachineLogic {
    private final RecipeRegistry recipeRegistry;
    private final ItemRegistry itemRegistry;

    public ProcessorMachine(RecipeRegistry recipeRegistry, ItemRegistry itemRegistry) {
        this.recipeRegistry = recipeRegistry;
        this.itemRegistry = itemRegistry;
    }

    @Override
    public void tick(MachineState state) {
        Optional<MachineRecipe> recipe = recipeRegistry.findMatching(state.inventory());
        if (recipe.isEmpty()) {
            state.setProgress(0);
            state.setActiveRecipe(null);
            return;
        }
        MachineRecipe active = recipe.get();
        state.setActiveRecipe(active.id());
        if (!state.consumeEnergy(active.energyPerTick())) {
            return;
        }
        state.setProgress(state.progress() + 1 + (state.level() / 3));
        if (state.progress() >= active.ticks()) {
            recipeRegistry.consumeIngredients(state.inventory(), active);
            ItemStack output = itemRegistry.get(active.outputId());
            state.inventory().addItem(output);
            state.setProgress(0);
        }
    }
}
