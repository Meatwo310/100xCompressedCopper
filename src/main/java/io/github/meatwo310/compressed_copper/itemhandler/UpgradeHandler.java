package io.github.meatwo310.compressed_copper.itemhandler;

import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class UpgradeHandler extends ItemStackHandler {
    public UpgradeHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(Items.TEST_UPGRADE_1.get());
    }
}
