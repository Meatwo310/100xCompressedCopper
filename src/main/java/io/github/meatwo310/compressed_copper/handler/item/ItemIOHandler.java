package io.github.meatwo310.compressed_copper.handler.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Manages item input and output for the machine.
 * Inputs are redirected to the InputHandler, and outputs are redirected to the OutputHandler.
 */
public class ItemIOHandler implements IItemHandler {
    private final ItemInputHandler itemInputHandler;
    private final ItemOutputHandler itemOutputHandler;

    public ItemIOHandler(ItemInputHandler itemInputHandler, ItemOutputHandler itemOutputHandler) {
        this.itemInputHandler = itemInputHandler;
        this.itemOutputHandler = itemOutputHandler;
    }

    @Override
    public int getSlots() {
        return itemInputHandler.getSlots() + itemOutputHandler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < itemInputHandler.getSlots()) {
            return itemInputHandler.getStackInSlot(slot);
        } else {
            return itemOutputHandler.getStackInSlot(slot - itemInputHandler.getSlots());
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        if (isWrongSlot(slot)) return itemStack;
        if (slot < itemInputHandler.getSlots()) {
            return itemInputHandler.insertItem(slot, itemStack, simulate);
        } else {
            return itemStack;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < itemInputHandler.getSlots()) {
            return ItemStack.EMPTY;
        } else {
            return itemOutputHandler.extractItem(slot - itemInputHandler.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return itemInputHandler.getSlotLimit(0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        if (isWrongSlot(slot)) return false;
        if (slot < itemInputHandler.getSlots()) {
            return itemInputHandler.isItemValid(slot, itemStack);
        } else {
            return itemOutputHandler.isItemValid(slot - itemInputHandler.getSlots(), itemStack);
        }
    }

    private boolean isWrongSlot(int slot) {
        return slot < 0 || slot >= getSlots();
    }
}
