package io.github.meatwo310.compressed_copper.handler.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FluidsHandler implements IFluidHandler {
    private final int tanks;
    private final int capacity;
    private final List<FluidStack> fluidStacks;

    public FluidsHandler(int tanks, int capacity) {
        this.tanks = tanks;
        this.capacity = capacity;
        this.fluidStacks = new ArrayList<>(tanks);
        for (int i = 0; i < tanks; i++) {
            fluidStacks.add(FluidStack.EMPTY);
        }
    }

    @Override
    public int getTanks() {
        return tanks;
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (isWrongSlot(tank)) return FluidStack.EMPTY;
        return fluidStacks.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) {
        if (isWrongSlot(tank)) return 0;
        return capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack) {
        if (isWrongSlot(tank)) return false;
        return fluidStacks.get(tank).isEmpty() || fluidStacks.get(tank).isFluidEqual(fluidStack);
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        if (fluidStack.isEmpty()) return 0;
        if (fluidAction.simulate()) {
            int filled = 0;
            for (int i = 0; i < tanks; i++) {
                if (!isFluidValid(i, fluidStack)) continue;

                filled += Math.min(fluidStack.getAmount() - filled, capacity - fluidStacks.get(i).getAmount());
                if (filled >= fluidStack.getAmount()) break;
            }
            return filled;
        } else {
            int remaining = fluidStack.getAmount();
            for (int i = 0; i < tanks; i++) {
                if (!isFluidValid(i, fluidStack)) continue;

                int toFill = Math.min(fluidStack.getAmount(), capacity - fluidStacks.get(i).getAmount());
                if (toFill <= 0) continue;

                if (fluidStacks.get(i).isEmpty()) {
                    fluidStacks.set(i, new FluidStack(fluidStack, toFill));
                } else {
                    fluidStacks.get(i).grow(toFill);
                }
                onContentsChanged(i);
                remaining -= toFill;
                if (remaining <= 0) break;
            }
            return fluidStack.getAmount() - remaining;
        }
    }

    @Override
    public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        if (fluidStack.isEmpty()) return FluidStack.EMPTY;
        int targetSlot = -1;
        for (int i = 0; i < tanks; i++) {
            if (!fluidStacks.get(i).isFluidEqual(fluidStack)) continue;
            targetSlot = i;
            break;
        }
        if (targetSlot == -1) return FluidStack.EMPTY;

        int drained = Math.min(fluidStack.getAmount(), fluidStacks.get(targetSlot).getAmount());
        FluidStack stack = new FluidStack(fluidStacks.get(targetSlot), drained);
        if (fluidAction.execute() && drained > 0) {
            fluidStacks.get(targetSlot).shrink(drained);
            onContentsChanged(targetSlot);
        }
        return stack;
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction fluidAction) {
        if (maxDrain <= 0) return FluidStack.EMPTY;
        FluidStack fluidStack = new FluidStack(fluidStacks.get(0), maxDrain);
        return drain(fluidStack, fluidAction);
    }

    /**
     * Clears all tanks and fills them with the given FluidStacks.
     * @param fluidStacks List of FluidStacks to fill the tanks
     */
    public void fillAll(List<FluidStack> fluidStacks) {
        if (fluidStacks.size() > this.tanks) throw new IllegalArgumentException("fluidStacks must not be larger than FluidsHandler tanks");
        this.clear();
        this.fluidStacks.addAll(fluidStacks.stream().map(FluidStack::copy).toList());
        while (this.fluidStacks.size() < tanks) {
            this.fluidStacks.add(FluidStack.EMPTY);
        }
    }

    /**
     * Drains all tanks of the given FluidStacks.
     * @param fluidStacks List of FluidStacks to drain
     * @param fluidAction FluidAction to perform
     * @return True if all FluidStacks were drained, false otherwise
     */
    public boolean drainAll(List<FluidStack> fluidStacks, FluidAction fluidAction) {
        return fluidStacks.stream()
                .map(fluidStack -> this.drain(fluidStack, fluidAction))
                .filter(fluidStack -> !fluidStack.isEmpty())
                .toList()
                .isEmpty();
    }

    /**
     * Clears all tanks.
     */
    private void clear() {
        fluidStacks.clear();
    }

    /**
     * Checks if the FluidsHandler contains the specified FluidStack.
     * Considers FluidStack amounts.
     * @param fluidStack FluidStack to verify
     * @return True if the FluidsHandler has sufficient FluidStack, false otherwise
     */
    public boolean containsFluid(FluidStack fluidStack) {
        return fluidStacks.stream().anyMatch(stack -> stack.containsFluid(fluidStack));
    }

    /**
     * Checks if the FluidsHandler contains all of the specified FluidStacks.
     * Considers FluidStack amounts.
     * @param fluidStacks List of FluidStacks to verify
     * @return True if the FluidsHandler has sufficient FluidStacks, false otherwise
     */
    public boolean containsFluids(List<FluidStack> fluidStacks) {
        return fluidStacks.stream().allMatch(this::containsFluid);
    }

    public boolean isEmpty() {
        return fluidStacks.stream().allMatch(FluidStack::isEmpty);
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        for (int i = 0; i < tanks; i++) {
            tag.put(String.valueOf(i), fluidStacks.get(i).writeToNBT(new CompoundTag()));
        }
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        for (int i = 0; i < tanks; i++) {
            fluidStacks.set(i, FluidStack.loadFluidStackFromNBT(tag.getCompound(String.valueOf(i))));
        }
    }

    private boolean isWrongSlot(int tank) {
        return tank < 0 || tank >= tanks;
    }

    protected void onContentsChanged(int tank) {
    }
}
