package io.github.meatwo310.compressed_copper.handler.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;

public class FluidsIOHandler implements IFluidHandler {
    private final FluidsInputHandler inputHandler;
    private final FluidsOutputHandler outputHandler;

    public FluidsIOHandler(FluidsInputHandler inputHandler, FluidsOutputHandler outputHandler) {
        this.inputHandler = inputHandler;
        this.outputHandler = outputHandler;
    }

    @Override
    public int getTanks() {
        return inputHandler.getTanks() + outputHandler.getTanks();
    }

    @Override
    public @NotNull FluidStack getFluidInTank(int tank) {
        if (isWrongSlot(tank)) return FluidStack.EMPTY;
        if (tank < inputHandler.getTanks()) {
            return inputHandler.getFluidInTank(tank);
        } else {
            return outputHandler.getFluidInTank(tank - inputHandler.getTanks());
        }
    }

    @Override
    public int getTankCapacity(int tank) {
        if (isWrongSlot(tank)) return 0;
        if (tank < inputHandler.getTanks()) {
            return inputHandler.getTankCapacity(tank);
        } else {
            return outputHandler.getTankCapacity(tank - inputHandler.getTanks());
        }
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack fluidStack) {
        if (isWrongSlot(tank)) return false;
        if (tank < inputHandler.getTanks()) {
            return inputHandler.isFluidValid(tank, fluidStack);
        } else {
            return outputHandler.isFluidValid(tank - inputHandler.getTanks(), fluidStack);
        }
    }

    @Override
    public int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return inputHandler.fill(fluidStack, fluidAction);
    }

    @Override
    public @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        return outputHandler.drain(fluidStack, fluidAction);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, FluidAction fluidAction) {
        return outputHandler.drain(maxDrain, fluidAction);
    }

    private boolean isWrongSlot(int slot) {
        return slot < 0 || slot >= getTanks();
    }
}
