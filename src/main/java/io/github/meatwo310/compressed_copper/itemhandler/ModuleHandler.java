package io.github.meatwo310.compressed_copper.itemhandler;

import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

public class ModuleHandler extends ItemStackHandler {
    public ModuleHandler(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack stack) {
        return stack.is(Items.TEST_MODULE_1.get());
    }
}
