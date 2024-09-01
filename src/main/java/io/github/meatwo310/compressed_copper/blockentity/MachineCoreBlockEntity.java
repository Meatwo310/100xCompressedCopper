package io.github.meatwo310.compressed_copper.blockentity;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.block.MachineCore;
import io.github.meatwo310.compressed_copper.config.Config;
import io.github.meatwo310.compressed_copper.handler.item.*;
import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import io.github.meatwo310.compressed_copper.recipe.CompressedMachineRecipe;
import io.github.meatwo310.compressed_copper.register.BlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
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

    public static final int SLOT_INPUT = 0;
    public static final int SLOT_OUTPUT = SLOT_INPUT + INPUT_SLOTS;
    public static final int SLOT_MODULE = SLOT_OUTPUT + OUTPUT_SLOTS;
    public static final int SLOT_UPGRADE = SLOT_MODULE + MODULE_SLOTS;

    private final InputHandler input = new InputHandler(INPUT_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final OutputHandler output = new OutputHandler(OUTPUT_SLOTS) {
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

    public final LazyOptional<InputHandler> inputLazyOptional = LazyOptional.of(() -> this.input);
    public final LazyOptional<OutputHandler> outputLazyOptional = LazyOptional.of(() -> this.output);
    public final LazyOptional<ModuleHandler> moduleLazyOptional = LazyOptional.of(() -> this.module);
    public final LazyOptional<UpgradeHandler> upgradeLazyOptional = LazyOptional.of(() -> this.upgrade);
    public final LazyOptional<ProcessingHandler> processingInputLazyOptional;
    public final LazyOptional<ProcessingHandler> processingOutputLazyOptional;

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
        processingInputLazyOptional = LazyOptional.of(() -> this.processingInput);
        processingOutputLazyOptional = LazyOptional.of(() -> this.processingOutput);
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
        if (cap != ForgeCapabilities.ITEM_HANDLER) return super.getCapability(cap, side);
        return LazyOptional.of(() -> new IOHandler(
                inputLazyOptional.orElse(new InputHandler(INPUT_SLOTS)),
                outputLazyOptional.orElse(new OutputHandler(OUTPUT_SLOTS))
        )).cast();
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
                    recipe.codec.getInputItems(),
                    recipe.codec.getOutputItems(),
                    CompressedMachineRecipe.getProcessingTime(be.getModule(), recipe.codec)
            );
        });
    }

    private Optional<CompressedMachineRecipe> getValidRecipe() {
        SimpleContainer container = new SimpleContainer(INPUT_SLOTS + OUTPUT_SLOTS + MODULE_SLOTS + UPGRADE_SLOTS);

        for (int i = 0; i < INPUT_SLOTS; i++)
            container.setItem(SLOT_INPUT + i, this.input.getStackInSlot(i));
        for (int i = 0; i < OUTPUT_SLOTS; i++)
            container.setItem(SLOT_OUTPUT + i, this.output.getStackInSlot(i));
        for (int i = 0; i < MODULE_SLOTS; i++)
            container.setItem(SLOT_MODULE + i, this.module.getStackInSlot(i));
        for (int i = 0; i < UPGRADE_SLOTS; i++)
            container.setItem(SLOT_UPGRADE + i, this.upgrade.getStackInSlot(i));

        return level == null ? Optional.empty() : level.getRecipeManager().getRecipeFor(CompressedMachineRecipe.Type.INSTANCE, container, level);
    }

    private void startProcessing(List<ItemStack> inputStacks, List<ItemStack> outputStacks, int ticks) {
        this.maxProgress = ticks;
        this.processingInput.copyItemStacks(inputStacks);
        this.processingOutput.copyItemStacks(outputStacks);
        this.input.consumeAllStacks(inputStacks);
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
        LogUtils.getLogger().debug("Move processing output to the output: {}", this.processingOutput.getAllStacks());
        for (int i = 0; i < this.processingOutput.getSlots(); i++) {
            ItemStack stack = this.processingOutput.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            ItemStack remaining = this.output.forceInsertItem(stack, false);
            this.processingOutput.setStackInSlot(i, remaining);
        }

        if (this.processingOutput.isEmpty()) {
            LogUtils.getLogger().debug("Done processing");
            this.progress = 0;
            this.maxProgress = 0;
            setChanged();
            return true;
        } else {
            // if the output is not empty, try to move the remaining items to the input next tick
            LogUtils.getLogger().debug("Processing output is not empty, try again next tick");
            this.progress--;
            setChanged();
            return false;
        }
    }
}
