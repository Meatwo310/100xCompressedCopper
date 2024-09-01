package io.github.meatwo310.compressed_copper.blockentity;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.block.MachineCore;
import io.github.meatwo310.compressed_copper.config.Config;
import io.github.meatwo310.compressed_copper.handler.fluid.FluidsIOHandler;
import io.github.meatwo310.compressed_copper.handler.fluid.FluidsInputHandler;
import io.github.meatwo310.compressed_copper.handler.fluid.FluidsOutputHandler;
import io.github.meatwo310.compressed_copper.handler.fluid.ProcessingFluidHandler;
import io.github.meatwo310.compressed_copper.handler.item.*;
import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import io.github.meatwo310.compressed_copper.recipe.CompressedMachineRecipe;
import io.github.meatwo310.compressed_copper.recipe.MachineData;
import io.github.meatwo310.compressed_copper.recipe.NotContainer;
import io.github.meatwo310.compressed_copper.register.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class MachineCoreBlockEntity extends BlockEntity implements MenuProvider {
    private static final Component TITLE =
            Component.translatable("container." + CompressedCopper.MODID + ".machine_core");

    public static final int INPUT_SLOTS = 4;
    public static final int OUTPUT_SLOTS = 4;
    public static final int MODULE_SLOTS = 1;
    public static final int UPGRADE_SLOTS = 5;

    public static final int FLUID_INPUT_TANKS = 4;
    public static final int FLUID_OUTPUT_TANKS = 4;

    public static final int FLUID_TANK_CAPACITY = Integer.MAX_VALUE;

    private final ItemStackInputHandler input = new ItemStackInputHandler(INPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final ItemStackOutputHandler output = new ItemStackOutputHandler(OUTPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final ModuleHandler module = new ModuleHandler(MODULE_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            setCustomName();
        }
    };
    private final UpgradeHandler upgrade = new UpgradeHandler(UPGRADE_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final ProcessingHandler processingInput;
    private final ProcessingHandler processingOutput;
    private final FluidsInputHandler fluidInput = new FluidsInputHandler(FLUID_INPUT_TANKS, FLUID_TANK_CAPACITY) {
        @Override
        protected void onContentsChanged(int tank) {
            super.onContentsChanged(tank);
            setChanged();
        }
    };
    private final FluidsOutputHandler fluidOutput = new FluidsOutputHandler(FLUID_OUTPUT_TANKS, FLUID_TANK_CAPACITY) {
        @Override
        protected void onContentsChanged(int tank) {
            super.onContentsChanged(tank);
            setChanged();
        }
    };
    private final ProcessingFluidHandler processingFluidInput;
    private final ProcessingFluidHandler processingFluidOutput;

    public final LazyOptional<ItemStackInputHandler> inputLazyOptional = LazyOptional.of(() -> this.input);
    public final LazyOptional<ItemStackOutputHandler> outputLazyOptional = LazyOptional.of(() -> this.output);
    public final LazyOptional<ModuleHandler> moduleLazyOptional = LazyOptional.of(() -> this.module);
    public final LazyOptional<UpgradeHandler> upgradeLazyOptional = LazyOptional.of(() -> this.upgrade);
    public final LazyOptional<ProcessingHandler> processingInputLazyOptional;
    public final LazyOptional<ProcessingHandler> processingOutputLazyOptional;
    public final LazyOptional<FluidsInputHandler> fluidInputLazyOptional = LazyOptional.of(() -> this.fluidInput);
    public final LazyOptional<FluidsOutputHandler> fluidOutputLazyOptional = LazyOptional.of(() -> this.fluidOutput);
    public final LazyOptional<ProcessingFluidHandler> processingFluidInputLazyOptional;
    public final LazyOptional<ProcessingFluidHandler> processingFluidOutputLazyOptional;

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 0;
    public static final int DATA_SIZE = 2;
    public static final int DATA_PROGRESS = 0;
    public static final int DATA_MAX_PROGRESS = 1;

    private Component customName = TITLE;

    public MachineCoreBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntities.MACHINE_CORE.get(), pos, state);

        processingInput = new ProcessingHandler(Config.MACHINE_CORE_MAX_PROCESSING_BUFFER.get()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
        processingOutput = new ProcessingHandler(Config.MACHINE_CORE_MAX_PROCESSING_BUFFER.get()) {
            @Override
            protected void onContentsChanged(int slot) {
                super.onContentsChanged(slot);
                setChanged();
            }
        };
        processingFluidInput = new ProcessingFluidHandler(Config.MACHINE_CORE_MAX_PROCESSING_BUFFER.get(), FLUID_TANK_CAPACITY) {
            @Override
            protected void onContentsChanged(int tank) {
                super.onContentsChanged(tank);
                setChanged();
            }
        };
        processingFluidOutput = new ProcessingFluidHandler(Config.MACHINE_CORE_MAX_PROCESSING_BUFFER.get(), FLUID_TANK_CAPACITY) {
            @Override
            protected void onContentsChanged(int tank) {
                super.onContentsChanged(tank);
                setChanged();
            }
        };

        processingInputLazyOptional = LazyOptional.of(() -> this.processingInput);
        processingOutputLazyOptional = LazyOptional.of(() -> this.processingOutput);
        processingFluidInputLazyOptional = LazyOptional.of(() -> this.processingFluidInput);
        processingFluidOutputLazyOptional = LazyOptional.of(() -> this.processingFluidOutput);

        this.data = new ContainerData() {
            @Override
            public int get(int pIndex) {
                return switch (pIndex) {
                    case DATA_PROGRESS -> MachineCoreBlockEntity.this.progress;
                    case DATA_MAX_PROGRESS -> MachineCoreBlockEntity.this.maxProgress;
                    default -> DATA_PROGRESS;
                };
            }

            @Override
            public void set(int pIndex, int pValue) {
                switch (pIndex) {
                    case DATA_PROGRESS -> MachineCoreBlockEntity.this.progress = pValue;
                    case DATA_MAX_PROGRESS -> MachineCoreBlockEntity.this.maxProgress = pValue;
                }
            }

            @Override
            public int getCount() {
                return DATA_SIZE;
            }
        };
    }

    public ItemStack getModule() {
        if (!moduleLazyOptional.isPresent()) return ItemStack.EMPTY;
        return moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0);
    }

    public Direction getDirection() {
        return this.getBlockState().getValue(MachineCore.FACING);
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) return LazyOptional.of(() -> new ItemStackIOHandler(
                inputLazyOptional.orElse(new ItemStackInputHandler(INPUT_SLOTS)),
                outputLazyOptional.orElse(new ItemStackOutputHandler(OUTPUT_SLOTS))
        )).cast();
        if (cap == ForgeCapabilities.FLUID_HANDLER) return LazyOptional.of(() -> new FluidsIOHandler(
                fluidInputLazyOptional.orElse(new FluidsInputHandler(FLUID_INPUT_TANKS, FLUID_TANK_CAPACITY)),
                fluidOutputLazyOptional.orElse(new FluidsOutputHandler(FLUID_OUTPUT_TANKS, FLUID_TANK_CAPACITY))
        )).cast();

        return super.getCapability(cap, side);
    }

    @Override
    public void load(@NotNull CompoundTag nbt) {
        super.load(nbt);
        CompoundTag data = nbt.getCompound(CompressedCopper.MODID);

        if (data.contains("input")) this.inputLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("input"))
        );
        if (data.contains("output")) this.outputLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("output"))
        );
        if (data.contains("module")) this.moduleLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("module"))
        );
        if (data.contains("upgrade")) this.upgradeLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("upgrade"))
        );
        if (data.contains("processingInput")) this.processingInputLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("processingInput"))
        );
        if (data.contains("processingOutput")) this.processingOutputLazyOptional.ifPresent(handler ->
                handler.deserializeNBT(data.getCompound("processingOutput"))
        );
        if (data.contains("fluidInput")) this.fluidInputLazyOptional.ifPresent(tank ->
                tank.deserializeNBT(data.getCompound("fluidInput"))
        );
        if (data.contains("fluidOutput")) this.fluidOutputLazyOptional.ifPresent(tank ->
                tank.deserializeNBT(data.getCompound("fluidOutput"))
        );
        if (data.contains("progress")) this.progress = data.getInt("progress");
        if (data.contains("maxProgress")) this.maxProgress = data.getInt("maxProgress");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag data = new CompoundTag();

        this.inputLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("input", handler.serializeNBT());
        });
        this.outputLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("output", handler.serializeNBT());
        });
        this.moduleLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("module", handler.serializeNBT());
        });
        this.upgradeLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("upgrade", handler.serializeNBT());
        });
        this.processingInputLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("processingInput", handler.serializeNBT());
        });
        this.processingOutputLazyOptional.ifPresent(handler -> {
            if (!handler.isEmpty()) data.put("processingOutput", handler.serializeNBT());
        });
        this.fluidInputLazyOptional.ifPresent(tank -> {
            if (!tank.isEmpty()) data.put("fluidInput", tank.serializeNBT());
        });
        this.fluidOutputLazyOptional.ifPresent(tank -> {
            if (!tank.isEmpty()) data.put("fluidOutput", tank.serializeNBT());
        });
        if (this.progress > 0)  data.putInt("progress", this.progress);
        if (this.maxProgress > 0) data.putInt("maxProgress", this.maxProgress);

        nbt.put(CompressedCopper.MODID, data);
    }

    @Override
    public void setRemoved() {
        this.inputLazyOptional.invalidate();
        this.outputLazyOptional.invalidate();
        this.moduleLazyOptional.invalidate();
        this.upgradeLazyOptional.invalidate();
        this.processingInputLazyOptional.invalidate();
        this.processingOutputLazyOptional.invalidate();
        this.fluidInputLazyOptional.invalidate();
        this.fluidOutputLazyOptional.invalidate();
        super.setRemoved();
    }

    @Override
    public void setChanged() {
        super.setChanged();
        if (this.level != null && this.level.isClientSide())
            this.level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_ALL); // update the block state
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        this.inputLazyOptional.invalidate();
        this.outputLazyOptional.invalidate();
        this.moduleLazyOptional.invalidate();
        this.upgradeLazyOptional.invalidate();
        this.processingInputLazyOptional.invalidate();
        this.processingOutputLazyOptional.invalidate();
        this.fluidInputLazyOptional.invalidate();
        this.fluidOutputLazyOptional.invalidate();
    }

    public void setCustomName() {
        if (!moduleLazyOptional.isPresent()) return;
        ItemStack module = moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0);
        customName = module.isEmpty() ? TITLE : Component.translatable(
                "container." + CompressedCopper.MODID + ".machine_core.custom",
                module.getHoverName()
        );
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return customName;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new MachineCoreMenu(id, inventory, this, data);
    }

    @SuppressWarnings("unused parameter")
    public static void tick(ServerLevel level, BlockPos pos, BlockState state, MachineCoreBlockEntity be) {
        // Set the custom name every 20 ticks
        // Note: Custom name is set automatically when the casing or module is changed
        if (level.getGameTime() % 20 == 0) be.setCustomName();

        // Check if the block entity is processing and increase the progress
        if (be.isProcessing()) {
            level.sendParticles(ParticleTypes.END_ROD, pos.getX() + 1, pos.getY() + 1.0, pos.getZ() + 1, 1, 0.0, 0.0, 0.0, 0.0);
            be.increaseProgress();
            if (!be.isProcessingFinished() || !be.finishProcessing()) {
                // Return early to skip unnecessary checks if the processing is not finished
                return;
            }
        }

        // Check if the module is valid
        if (!be.moduleLazyOptional.isPresent()) return;
        if (be.moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0).isEmpty()) return;

        // Check the input and start processing
        Optional<CompressedMachineRecipe> optionalRecipe = be.getValidRecipe();
        optionalRecipe.ifPresent(recipe -> {
            LogUtils.getLogger().debug("Valid recipe found: {}", recipe);
            be.startProcessing(
                    recipe.codec.inputItems(),
                    recipe.codec.outputItems(),
                    recipe.codec.inputFluids(),
                    recipe.codec.outputFluids(),
                    CompressedMachineRecipe.getProcessingTime(be.getModule(), recipe.codec)
            );
        });
    }

    private Optional<CompressedMachineRecipe> getValidRecipe() {
        if (level == null) return Optional.empty();
        return level.getRecipeManager().getRecipeFor(
                CompressedMachineRecipe.Type.INSTANCE,
                new NotContainer(new MachineData(
                        input,
                        output,
                        module,
                        upgrade,
                        processingInput,
                        processingOutput,
                        fluidInput,
                        fluidOutput
                )),
                level
        );
    }

    private void startProcessing(List<ItemStack> inputStacks, List<ItemStack> outputStacks, List<FluidStack> inputFluids, List<FluidStack> outputFluids, int ticks) {
        this.maxProgress = ticks;
        this.processingInput.copyItemStacks(inputStacks);
        this.processingOutput.copyItemStacks(outputStacks);
        this.processingFluidInput.fillAll(inputFluids);
        this.processingFluidOutput.fillAll(outputFluids);
        this.input.consumeAllStacks(inputStacks);
        this.fluidInput.drainAll(inputFluids, IFluidHandler.FluidAction.EXECUTE);
        setChanged();
    }

    private boolean isProcessing() {
        return this.maxProgress > 0;
    }

    private boolean isProcessingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseProgress() {
        this.progress++;
        setChanged();
    }

    private boolean finishProcessing() {
        // empty the processing input
        this.processingInput.clear();

        // move the processing output to the output
        for (int i = 0; i < this.processingOutput.getSlots(); i++) {
            ItemStack stack = this.processingOutput.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemStack remaining = this.output.forceInsertItem(stack, false);
            this.processingOutput.setStackInSlot(i, remaining);
        }
        for (int i = 0; i < this.processingFluidOutput.getTanks(); i++) {
            FluidStack stack = this.processingFluidOutput.getFluidInTank(i);
            if (stack.isEmpty()) continue;
            int remaining = this.fluidOutput.fill(stack, IFluidHandler.FluidAction.EXECUTE);
            this.processingFluidOutput.drain(remaining, IFluidHandler.FluidAction.EXECUTE);
        }

        if (this.processingOutput.isEmpty() && this.processingFluidOutput.isEmpty()) {
            LogUtils.getLogger().debug("Processing done!");
            this.progress = 0;
            this.maxProgress = 0;
            setChanged();
            return true;
        } else {
            // if the output is not empty, try to move the remaining items to the input next tick
            LogUtils.getLogger().debug("Cannot output result, try again next tick");
            this.progress--;
            setChanged();
            return false;
        }
    }
}
