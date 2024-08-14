package io.github.meatwo310.compressed_copper.itemhandler;

import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class InputHandler extends ItemStackHandlerPlus {
    public InputHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return true;
    }
}
