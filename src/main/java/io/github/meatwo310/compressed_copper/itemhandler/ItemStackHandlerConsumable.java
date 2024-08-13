package io.github.meatwo310.compressed_copper.itemhandler;

import net.minecraft.core.NonNullList;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import java.util.LinkedHashMap;
import java.util.Map;

public class ItemStackHandlerConsumable extends ItemStackHandler {
    public ItemStackHandlerConsumable() {
        super();
    }
    public ItemStackHandlerConsumable(int size) {
        super(size);
    }
    public ItemStackHandlerConsumable(NonNullList<ItemStack> stacks) {
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

    /**
     * Remove all of the ItemStack from the inventory if it has enough
     * @param stack ItemStack to consume all
     * @return True if the all of the ItemStack was consumed, false otherwise
     */
    public boolean consumeAll(ItemStack stack) {
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
}
