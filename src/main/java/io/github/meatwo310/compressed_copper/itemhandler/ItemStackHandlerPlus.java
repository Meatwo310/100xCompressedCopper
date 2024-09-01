package io.github.meatwo310.compressed_copper.itemhandler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * A custom ItemStackHandler with additional functionality for managing ItemStacks.
 */
public class ItemStackHandlerPlus extends ItemStackHandler {
    public ItemStackHandlerPlus() {
        super();
    }
    public ItemStackHandlerPlus(int size) {
        super(size);
    }
    public ItemStackHandlerPlus(NonNullList<ItemStack> stacks) {
        super(stacks);
    }

    /**
     * Retrieves all ItemStacks in the inventory, merged by their Item.
     * @return Map of Items and their total count
     */
    public Map<Item, Integer> getAllStacks() {
        Map<Item, Integer> stacks = new LinkedHashMap<>();
        for (int i = 0; i < getSlots(); i++) {
            ItemStack stack = getStackInSlot(i);
            if (!stack.isEmpty()) {
                stacks.merge(stack.getItem(), stack.getCount(), Integer::sum);
            }
        }
        return stacks;
    }

    /**
     * Checks if the inventory contains the specified ItemStack.
     * @param stack ItemStack to check
     * @return True if the inventory has enough of the ItemStack, false otherwise
     */
    public boolean hasStack(ItemStack stack) {
        Map<Item, Integer> stacks = getAllStacks();
        return stacks.containsKey(stack.getItem()) && stacks.get(stack.getItem()) >= stack.getCount();
    }

    /**
     * Checks if the inventory contains all specified ItemStacks.
     * @param stacks List of ItemStacks to check
     * @return True if the inventory has enough of all ItemStacks, false otherwise
     */
    public boolean hasStacks(List<ItemStack> stacks) {
        return stacks.stream().allMatch(this::hasStack);
    }

    /**
     * Checks if the inventory is empty.
     * @return True if the inventory is empty, false otherwise
     */
    public boolean isEmpty() {
        return IntStream.range(0, getSlots())
                .allMatch(i -> getStackInSlot(i).isEmpty());
    }

    /**
     * Consumes the entire ItemStack from the inventory if enough is available.
     * @param stack ItemStack to consume
     * @return True if the entire ItemStack was consumed, false otherwise
     */
    public boolean consumeAllStack(ItemStack stack) {
        ItemStack toConsume = stack.copy();
        if (!hasStack(stack)) {
            return false;
        }
        for (int i = 0; i < getSlots(); i++) {
            ItemStack slotStack = getStackInSlot(i);
            if (slotStack.isEmpty() || !ItemStack.isSameItem(stack, slotStack)) {
                continue;
            }
            int toConsumeCount = Math.min(toConsume.getCount(), slotStack.getCount());
            toConsume.shrink(toConsumeCount);
            slotStack.shrink(toConsumeCount);
            this.setStackInSlot(i, slotStack);
            if (toConsume.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Consumes all specified ItemStacks from the inventory if available.
     *
     * @param stacks List of ItemStacks to consume
     * @return True if all ItemStacks were consumed, false otherwise
     * @throws IllegalStateException If unable to consume all ItemStacks
     */
    public boolean consumeAllStacks(List<ItemStack> stacks) {
        if (!hasStacks(stacks)) return false;
        if (!stacks.stream().allMatch(this::consumeAllStack))
            throw new IllegalStateException("Failed to consume all stacks");
        return true;
    }

    /**
     * Inserts an ItemStack into the specified slot, bypassing normal checks.
     * @param slot Slot to insert the ItemStack
     * @param stack ItemStack to insert
     * @param simulate If true, the ItemStack will not be inserted
     * @return The remaining ItemStack that could not be inserted
     */
    public ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    /**
     * Inserts an ItemStack into available slots, bypassing normal checks.
     * @param stack ItemStack to insert
     * @param simulate If true, the ItemStack will not be inserted
     * @return The remaining ItemStack that could not be inserted
     */
    public ItemStack forceInsertItem(@NotNull ItemStack stack, boolean simulate) {
        for (int i = 0; i < getSlots(); i++) {
            stack = forceInsertItem(i, stack, simulate);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    /**
     * Clears all ItemStacks from the inventory.
     */
    public void clear() {
        for (int i = 0; i < getSlots(); i++) {
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    /**
     * Copies a list of ItemStacks into the inventory.
     *
     * @param stacks List of ItemStacks to copy
     * @throws IllegalArgumentException if the list size exceeds the inventory size
     */
    public void copyItemStacks(List<ItemStack> stacks) {
        if (stacks.size() > getSlots())
            throw new IllegalArgumentException("Stacks list must not exceed inventory size");

        for (int i = 0; i < stacks.size(); i++) {
            setStackInSlot(i, stacks.get(i).copy());
        }
    }
}
