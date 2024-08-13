package io.github.meatwo310.compressed_copper.itemhandler;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class OutputHandler extends ItemStackHandlerConsumable {
    public OutputHandler(int size) {
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

    public ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }
}
