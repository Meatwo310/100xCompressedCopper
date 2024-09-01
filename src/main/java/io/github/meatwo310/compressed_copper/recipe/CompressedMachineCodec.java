package io.github.meatwo310.compressed_copper.recipe;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

import java.util.List;

public record CompressedMachineCodec(
        Item module,
        int minTier,
        int maxTier,
        int minTierTicks,
        int maxTierTicks,
        List<ItemStack> inputItems,
        List<ItemStack> outputItems,
        List<FluidStack> inputFluids,
        List<FluidStack> outputFluids
) {
}
