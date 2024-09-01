package io.github.meatwo310.compressed_copper.handler.item;

import io.github.meatwo310.compressed_copper.data.ItemTags;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class UpgradeHandler extends ItemStackHandlerPlus {
    public UpgradeHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.getTags().anyMatch(tag -> Objects.equals(tag, ItemTags.UPGRADES));
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }
}
