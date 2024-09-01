package io.github.meatwo310.compressed_copper.handler.item;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ItemInputHandler extends ItemStackHandlerPlus {
    public ItemInputHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
