package io.github.meatwo310.compressed_copper.itemhandler;

import io.github.meatwo310.compressed_copper.data.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CoverHandler extends ItemStackHandlerPlus {
    public CoverHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getTags().anyMatch(tag -> tag.equals(ItemTags.COVERS));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
