package io.github.meatwo310.compressed_copper.handler.item;

import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;

/**
 * Manages item input and output for the machine.
 * Inputs are redirected to the InputHandler, and outputs are redirected to the OutputHandler.
 */
public class IOHandler implements IItemHandler {
    private final InputHandler inputHandler;
    private final OutputHandler outputHandler;

    public IOHandler(InputHandler inputHandler, OutputHandler outputHandler) {
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    public int getSlots() {
        return inputHandler.getSlots() + outputHandler.getSlots();
    }

    @Override
    public @NotNull ItemStack getStackInSlot(int slot) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < inputHandler.getSlots()) {
            return inputHandler.getStackInSlot(slot);
        } else {
            return outputHandler.getStackInSlot(slot - inputHandler.getSlots());
        }
    }

    @Override
    public @NotNull ItemStack insertItem(int slot, @NotNull ItemStack itemStack, boolean simulate) {
        if (isWrongSlot(slot)) return itemStack;
        if (slot < inputHandler.getSlots()) {
            return inputHandler.insertItem(slot, itemStack, simulate);
        } else {
            return itemStack;
        }
    }

    @Override
    public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (isWrongSlot(slot)) return ItemStack.EMPTY;
        if (slot < inputHandler.getSlots()) {
            return ItemStack.EMPTY;
        } else {
            return outputHandler.extractItem(slot - inputHandler.getSlots(), amount, simulate);
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return inputHandler.getSlotLimit(0);
    }

    @Override
    public boolean isItemValid(int slot, @NotNull ItemStack itemStack) {
        if (isWrongSlot(slot)) return false;
        if (slot < inputHandler.getSlots()) {
            return inputHandler.isItemValid(slot, itemStack);
        } else {
            return outputHandler.isItemValid(slot - inputHandler.getSlots(), itemStack);
        }
    }

    private boolean isWrongSlot(int slot) {
        return slot < 0 || slot >= getSlots();
    }
}
