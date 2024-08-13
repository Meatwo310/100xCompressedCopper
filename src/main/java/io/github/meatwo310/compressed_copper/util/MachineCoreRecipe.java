package io.github.meatwo310.compressed_copper.util;

import net.minecraft.world.item.ItemStack;

import java.util.List;

public class MachineCoreRecipe {
    public final List<ItemStack> inputs;
    public final List<ItemStack> outputs;
    public final int ticks;

    public MachineCoreRecipe(List<ItemStack> inputs, List<ItemStack> outputs, int ticks) {
        this.inputs = inputs;
        this.outputs = outputs;
        this.ticks = ticks;
    }
}
