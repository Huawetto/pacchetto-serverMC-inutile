package com.pacchetto.recipe;

import java.util.List;

public record MachineRecipe(String id, List<String> inputIds, String outputId, int ticks, int energyPerTick) {}
