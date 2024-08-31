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

    public boolean hasStack(ItemStack stack) {
        Map<Item, Integer> stacks = getAllStacks();
        return stacks.containsKey(stack.getItem()) && stacks.get(stack.getItem()) >= stack.getCount();
    }

    public boolean hasStacks(List<ItemStack> stacks) {
        return stacks.stream().allMatch(this::hasStack);
    }

    public boolean isEmpty() {
        return IntStream.range(0, getSlots())
                .allMatch(i -> getStackInSlot(i).isEmpty());
    }

    /**
     * Remove all of the ItemStack from the inventory if it has enough
     * @param stack ItemStack to consume all
     * @return True if the all of the ItemStack was consumed, false otherwise
     */
    public boolean consumeAllStack(ItemStack stack) {
        var toConsume = stack.copy();
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

    public boolean consumeAllStacks(List<ItemStack> stacks) {
        if (!hasStacks(stacks)) return false;
        if (!stacks.stream().allMatch(this::consumeAllStack))
            throw new IllegalStateException("Failed to consume all stacks");
        return true;
    }

    public ItemStack forceInsertItem(int slot, @NotNull ItemStack stack, boolean simulate) {
        return super.insertItem(slot, stack, simulate);
    }

    public ItemStack forceInsertItem(@NotNull ItemStack stack, boolean simulate) {
        for (int i = 0; i < getSlots(); i++) {
            stack = forceInsertItem(i, stack, simulate);
            if (stack.isEmpty()) return ItemStack.EMPTY;
        }
        return stack;
    }

    public void empty() {
        for (int i = 0; i < getSlots(); i++) {
            setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    public void copyItemStacks(List<ItemStack> stacks) {
        if (stacks.size() > getSlots())
            throw new IllegalArgumentException("Stacks list is larger than the inventory size");

        for (int i = 0; i < stacks.size(); i++) {
            setStackInSlot(i, stacks.get(i).copy());
        }
    }
}
