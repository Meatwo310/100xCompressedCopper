package io.github.meatwo310.compressed_copper.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class CompressedMachineCodec {
    private final Item module;
    private final int minTier;
    private final int maxTier;
    private final int minTierTicks;
    private final int maxTierTicks;

    private final List<ItemStack> inputItems;
    private final List<ItemStack> outputItems;
//    private final Long inputEnergy;
//    private final Long outputEnergy;


    public CompressedMachineCodec(Item module, int minTier, int maxTier, int minTierTicks, int maxTierTicks, List<ItemStack> inputItems, List<ItemStack> outputItems) {
        this.module = module;
        this.minTier = minTier;
        this.maxTier = maxTier;
        this.minTierTicks = minTierTicks;
        this.maxTierTicks = maxTierTicks;
        this.inputItems = inputItems;
        this.outputItems = outputItems;
    }

    public Item getModule() {
        return module;
    }

    public int getMinTier() {
        return minTier;
    }

    public int getMaxTier() {
        return maxTier;
    }

    public int getMinTierTicks() {
        return minTierTicks;
    }

    public int getMaxTierTicks() {
        return maxTierTicks;
    }

    public List<ItemStack> getInputItems() {
        return inputItems;
    }

    public List<ItemStack> getOutputItems() {
        return outputItems;
    }

    @Override
    public String toString() {
        return "CompressedMachineCodec{" +
                "module=" + module +
                ", minTier=" + minTier +
                ", maxTier=" + maxTier +
                ", minTierTicks=" + minTierTicks +
                ", maxTierTicks=" + maxTierTicks +
                ", inputItems=" + inputItems +
                ", outputItems=" + outputItems +
                '}';
    }
}
