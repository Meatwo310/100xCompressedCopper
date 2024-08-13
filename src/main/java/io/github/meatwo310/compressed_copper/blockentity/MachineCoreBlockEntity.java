package io.github.meatwo310.compressed_copper.blockentity;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.block.MachineCore;
import io.github.meatwo310.compressed_copper.config.Config;
import io.github.meatwo310.compressed_copper.itemhandler.*;
import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import io.github.meatwo310.compressed_copper.register.BlockEntities;
import io.github.meatwo310.compressed_copper.util.MachineCoreRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class MachineCoreBlockEntity extends BlockEntity implements MenuProvider {
    private static final Component TITLE =
            Component.translatable("container." + CompressedCopper.MODID + ".machine_core");
    public static final int INPUT_SLOTS = 4;
    public static final int OUTPUT_SLOTS = 4;
    public static final int COVER_SLOTS = 1;
    public static final int MODULE_SLOTS = 1;
    public static final int UPGRADE_SLOTS = 3;

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
    private final CoversHandler casing = new CoversHandler(COVER_SLOTS) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            setCustomName();
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
    private final ItemStackHandler processingInput;
    private final ItemStackHandler processingOutput;

    public final LazyOptional<ItemStackHandler> inputLazyOptional = LazyOptional.of(() -> this.input);
    public final LazyOptional<ItemStackHandler> outputLazyOptional = LazyOptional.of(() -> this.output);
    public final LazyOptional<IItemHandler> casingLazyOptional = LazyOptional.of(() -> this.casing);
    public final LazyOptional<IItemHandler> moduleLazyOptional = LazyOptional.of(() -> this.module);
    public final LazyOptional<IItemHandler> upgradeLazyOptional = LazyOptional.of(() -> this.upgrade);
    public final LazyOptional<ItemStackHandler> processingInputLazyOptional;
    public final LazyOptional<ItemStackHandler> processingOutputLazyOptional;

    private int progress = 0;
    private int maxProgress = 0;

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
        if (side == Direction.DOWN) return this.outputLazyOptional.cast();
        return this.inputLazyOptional.cast();
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
        if (data.contains("casing")) this.casingLazyOptional.ifPresent(handler ->
                ((CoversHandler) handler).deserializeNBT(data.getCompound("casing"))
        );
        if (data.contains("module")) this.moduleLazyOptional.ifPresent(handler ->
                ((ModuleHandler) handler).deserializeNBT(data.getCompound("module"))
        );
        if (data.contains("upgrade")) this.upgradeLazyOptional.ifPresent(handler ->
                ((UpgradeHandler) handler).deserializeNBT(data.getCompound("upgrade"))
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

        this.inputLazyOptional.ifPresent(handler ->
                data.put("input", handler.serializeNBT())
        );
        this.outputLazyOptional.ifPresent(handler ->
                data.put("output", handler.serializeNBT())
        );
        this.casingLazyOptional.ifPresent(handler ->
                data.put("casing", ((CoversHandler) handler).serializeNBT())
        );
        this.moduleLazyOptional.ifPresent(handler ->
                data.put("module", ((ModuleHandler) handler).serializeNBT())
        );
        this.upgradeLazyOptional.ifPresent(handler ->
                data.put("upgrade", ((UpgradeHandler) handler).serializeNBT())
        );
        this.processingInputLazyOptional.ifPresent(handler ->
                data.put("processingInput", handler.serializeNBT())
        );
        this.processingOutputLazyOptional.ifPresent(handler ->
                data.put("processingOutput", handler.serializeNBT())
        );
        if (this.progress > 0)
            data.putInt("progress", this.progress);
        if (this.maxProgress > 0)
            data.putInt("maxProgress", this.maxProgress);

        nbt.put(CompressedCopper.MODID, data);
    }

    @Override
    public void setRemoved() {
        this.inputLazyOptional.invalidate();
        this.outputLazyOptional.invalidate();
        this.casingLazyOptional.invalidate();
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
        this.casingLazyOptional.invalidate();
        this.moduleLazyOptional.invalidate();
        this.upgradeLazyOptional.invalidate();
        this.processingInputLazyOptional.invalidate();
        this.processingOutputLazyOptional.invalidate();
    }

    public void setCustomName() {
        if (!casingLazyOptional.isPresent() || !moduleLazyOptional.isPresent()) return;
        ItemStack casing = casingLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0);
        ItemStack module = moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0);
        customName = casing.isEmpty() || module.isEmpty() ? TITLE : Component.translatable(
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
        return new MachineCoreMenu(id, inventory, this);
    }

    @SuppressWarnings("unused parameter")
    public static void tick(Level level, BlockPos pos, BlockState state, MachineCoreBlockEntity be) {
        if (level.isClientSide()) return;

        // Set the custom name every 20 ticks
        // Note: Custom name is set automatically when the casing or module is changed
        if (level.getGameTime() % 20 == 0) be.setCustomName();

        // Check if the block entity is processing and increase the progress
        if (be.isProcessing()) {
            be.increaseProgress();
            if (be.isProcessingFinished()) {
                be.finishProcessing();
            } else {
                // Return early to skip unnecessary checks if the processing is not finished
                return;
            }
        }

        // Check if the module is valid
        // TODO: Check the module and use it to determine the processing
        if (!be.moduleLazyOptional.isPresent()) return;
        if (be.moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0).isEmpty()) return;

        // Check the input and start processing
        // Temporary uses hardcoded recipe:
        //   Copper Block @ 9 -> 1x Compressed Copper Block @ 1
        //   1x Compacted Copper Block @ 9 -> 2x Compressed Copper Block @ 1
        // TODO: Check the JSON recipe cache
        // TODO: Progress bar
        MachineCoreRecipe recipe = be.getValidRecipe();
        if (recipe == null) return;
        be.startProcessing(recipe.inputs.get(0), recipe.outputs.get(0), recipe.ticks);
    }

    @Nullable
    private MachineCoreRecipe getValidRecipe() {
        List<MachineCoreRecipe> recipes = new ArrayList<>();
        recipes.add(new MachineCoreRecipe(
                List.of(new ItemStack(
                        RegistryObject.create(
                                new ResourceLocation("minecraft:copper_block"),
                                ForgeRegistries.ITEMS
                        ).get(),
                        9
                )),
                List.of(new ItemStack(
                        RegistryObject.create(
                            new ResourceLocation("allcompressedblock:compressed_copper_block_item_1"),
                            ForgeRegistries.ITEMS
                        ).get(),
                        1
                )),
                4
        ));
        recipes.add(new MachineCoreRecipe(
                List.of(new ItemStack(
                        RegistryObject.create(
                                new ResourceLocation("allcompressedblock:compressed_copper_block_item_1"),
                                ForgeRegistries.ITEMS
                        ).get(),
                        9
                )),
                List.of(new ItemStack(
                        RegistryObject.create(
                                new ResourceLocation("allcompressedblock:compressed_copper_block_item_2"),
                                ForgeRegistries.ITEMS
                        ).get(),
                        1
                )),
                4
        ));

        for (MachineCoreRecipe recipe : recipes) {
            if (recipe.inputs.stream().allMatch(this.input::hasStack))
                return recipe;
        }

        return null;
    }

    private void startProcessing(ItemStack inputStack, ItemStack outputStack, int ticks) {
        this.maxProgress = ticks;
        this.processingInput.setStackInSlot(0, inputStack);
        this.processingOutput.setStackInSlot(0, outputStack);
        this.input.consumeAll(inputStack);
    }

    private boolean isProcessing() {
        return this.maxProgress > 0;
    }

    private boolean isProcessingFinished() {
        return this.progress >= this.maxProgress;
    }

    private void increaseProgress() {
        this.progress++;
    }

    private void finishProcessing() {
        // empty the processing input
        for (int i = 0; i < this.processingInput.getSlots(); i++) {
            this.processingInput.setStackInSlot(i, ItemStack.EMPTY);
        }

        // move the processing output to the output
        for (int i = 0; i < this.processingOutput.getSlots(); i++) {
            ItemStack stack = this.processingOutput.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            this.processingOutput.setStackInSlot(i, ItemStack.EMPTY);
            this.output.forceInsertItem(0, stack, false);
        }

        // reset the progress
        this.progress = 0;
        this.maxProgress = 0;
    }
}
