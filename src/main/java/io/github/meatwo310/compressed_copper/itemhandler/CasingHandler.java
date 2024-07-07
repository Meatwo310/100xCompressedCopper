package io.github.meatwo310.compressed_copper.itemhandler;

import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class CasingHandler extends ItemStackHandler {
    public CasingHandler(int size) {
        super(size);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(Items.MACHINE_CASING_1.get());
    }
}
