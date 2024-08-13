package io.github.meatwo310.compressed_copper.itemhandler;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ProcessingHandler extends ItemStackHandlerConsumable {
    public ProcessingHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return stack;
    }
}
