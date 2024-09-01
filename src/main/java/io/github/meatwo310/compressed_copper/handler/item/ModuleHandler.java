package io.github.meatwo310.compressed_copper.handler.item;

import io.github.meatwo310.compressed_copper.data.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class ModuleHandler extends ItemStackHandlerPlus {
    public ModuleHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getTags().anyMatch(tag -> tag.equals(ItemTags.MODULES));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
