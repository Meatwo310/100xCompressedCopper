package io.github.meatwo310.compressed_copper.blockentity;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.itemhandler.*;
import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import io.github.meatwo310.compressed_copper.register.TileEntities;
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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineCoreTile extends BlockEntity implements MenuProvider {
    private static final Component TITLE =
            Component.translatable("container." + CompressedCopper.MODID + ".machine_core");
    private final ItemStackHandler input = new InputHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final ItemStackHandler output = new OutputHandler(4) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    private final IItemHandler casing = new CasingHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            setCustomName();
        }
    };
    private final IItemHandler module = new ModuleHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
            setCustomName();
        }
    };
    private final IItemHandler upgrade = new UpgradeHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            setChanged();
        }
    };
    public final LazyOptional<ItemStackHandler> inputLazyOptional = LazyOptional.of(() -> this.input);
    public final LazyOptional<ItemStackHandler> outputLazyOptional = LazyOptional.of(() -> this.output);
    public final LazyOptional<IItemHandler> casingLazyOptional = LazyOptional.of(() -> this.casing);
    public final LazyOptional<IItemHandler> moduleLazyOptional = LazyOptional.of(() -> this.module);
    public final LazyOptional<IItemHandler> upgradeLazyOptional = LazyOptional.of(() -> this.upgrade);

    private Component customName = TITLE;


    public MachineCoreTile(BlockPos pos, BlockState state) {
        super(TileEntities.MACHINE_CORE.get(), pos, state);
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
                ((CasingHandler) handler).deserializeNBT(data.getCompound("casing"))
        );
        if (data.contains("module")) this.moduleLazyOptional.ifPresent(handler ->
                ((ModuleHandler) handler).deserializeNBT(data.getCompound("module"))
        );
        if (data.contains("upgrade")) this.upgradeLazyOptional.ifPresent(handler ->
                ((UpgradeHandler) handler).deserializeNBT(data.getCompound("upgrade"))
        );
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag nbt) {
        super.saveAdditional(nbt);
        CompoundTag data = new CompoundTag();
        this.inputLazyOptional.ifPresent(handler -> data.put("input", handler.serializeNBT()));
        this.outputLazyOptional.ifPresent(handler -> data.put("output", handler.serializeNBT()));
        this.casingLazyOptional.ifPresent(handler -> data.put("casing", ((CasingHandler) handler).serializeNBT()));
        this.moduleLazyOptional.ifPresent(handler -> data.put("module", ((ModuleHandler) handler).serializeNBT()));
        this.upgradeLazyOptional.ifPresent(handler -> data.put("upgrade", ((UpgradeHandler) handler).serializeNBT()));
        nbt.put(CompressedCopper.MODID, data);
    }

    @Override
    public void setRemoved() {
        this.inputLazyOptional.invalidate();
        this.outputLazyOptional.invalidate();
        this.casingLazyOptional.invalidate();
        this.moduleLazyOptional.invalidate();
        this.upgradeLazyOptional.invalidate();
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
    }

    @SuppressWarnings("unused parameter")
    public static void tick(Level level, BlockPos pos, BlockState state, MachineCoreTile tile) {
        if (level.isClientSide()) return;

        if (level.getGameTime() % 20 == 0) tile.setCustomName();

        // check if the module is valid
        if (!tile.moduleLazyOptional.isPresent()) return;
        if (tile.moduleLazyOptional.orElseThrow(NullPointerException::new).getStackInSlot(0).isEmpty()) return;

        // show particles and play sounds every 20 ticks
        if (level.getGameTime() % 20 == 0 && level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ParticleTypes.END_ROD, pos.getX() + 1, pos.getY() + 1.0, pos.getZ() + 1, 1, 0.0, 0.0, 0.0, 0.0);
//            level.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_BREAK, SoundSource.BLOCKS, 0.75f, 1.0f);
        }
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
}
