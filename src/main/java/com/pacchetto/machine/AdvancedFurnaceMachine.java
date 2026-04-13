package com.pacchetto.machine;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class AdvancedFurnaceMachine implements MachineLogic {

    @Override
    public void tick(MachineState state) {
        ItemStack input = state.inventory().getItem(10);
        if (input == null) return;
        Material resultType = switch (input.getType()) {
            case IRON_ORE, DEEPSLATE_IRON_ORE, RAW_IRON -> Material.IRON_INGOT;
            case GOLD_ORE, DEEPSLATE_GOLD_ORE, RAW_GOLD -> Material.GOLD_INGOT;
            case SAND -> Material.GLASS;
            case COBBLESTONE -> Material.STONE;
            default -> null;
        };
        if (resultType == null) return;
        int cost = Math.max(4, 10 - state.level());
        if (!state.consumeEnergy(cost)) return;
        state.setProgress(state.progress() + 1);
        if (state.progress() >= 80) {
            ItemStack output = state.inventory().getItem(16);
            if (output != null && output.getType() != resultType) return;
            input.setAmount(input.getAmount() - 1);
            if (input.getAmount() <= 0) state.inventory().setItem(10, null);
            if (output == null) {
                state.inventory().setItem(16, new ItemStack(resultType, 1));
            } else {
                output.setAmount(Math.min(64, output.getAmount() + 1));
                state.inventory().setItem(16, output);
            }
            state.setProgress(0);
        }
    }
}
