package io.github.meatwo310.compressed_copper.handler.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Manages item input and output for the machine.
 * Inputs are redirected to the InputHandler, and outputs are redirected to the OutputHandler.
 */
public class ItemStackIOHandler implements IItemHandler {
    private final ItemStackInputHandler itemStackInputHandler;
    private final ItemStackOutputHandler itemStackOutputHandler;

    public ItemStackIOHandler(ItemStackInputHandler itemStackInputHandler, ItemStackOutputHandler itemStackOutputHandler) {
        this.itemStackInputHandler = itemStackInputHandler;
        this.itemStackOutputHandler = itemStackOutputHandler;
    }

    @Override
    public int getSlots() {
        return itemStackInputHandler.getSlots() + itemStackOutputHandler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < itemStackInputHandler.getSlots()) {
            return itemStackInputHandler.getStackInSlot(slot);
        } else {
            return itemStackOutputHandler.getStackInSlot(slot - itemStackInputHandler.getSlots());
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        if (isWrongSlot(slot)) return itemStack;
        if (slot < itemStackInputHandler.getSlots()) {
            return itemStackInputHandler.insertItem(slot, itemStack, simulate);
        } else {
            return itemStack;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < itemStackInputHandler.getSlots()) {
            return ItemStack.EMPTY;
        } else {
            return itemStackOutputHandler.extractItem(slot - itemStackInputHandler.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemStackInputHandler.getSlotLimit(0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        if (isWrongSlot(slot)) return false;
        if (slot < itemStackInputHandler.getSlots()) {
            return itemStackInputHandler.isItemValid(slot, itemStack);
        } else {
            return itemStackOutputHandler.isItemValid(slot - itemStackInputHandler.getSlots(), itemStack);
        }
    }

    private boolean isWrongSlot(int slot) {
        return slot < 0 || slot >= getSlots();
    }
}
