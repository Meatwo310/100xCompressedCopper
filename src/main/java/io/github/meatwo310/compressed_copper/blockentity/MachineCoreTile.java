package io.github.meatwo310.compressed_copper.blockentity;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.register.TileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public class MachineCoreTile extends BlockEntity {
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        protected void onContentsChanged(int slot) {
            super.onContentsChanged(slot);
            MachineCoreTile.this.setChanged();
        }
    };
    private final LazyOptional<IItemHandler> optional = LazyOptional.of(() -> inventory);

    public MachineCoreTile(BlockPos pos, BlockState state) {
        super(TileEntities.MACHINE_CORE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap) {
        return cap == ForgeCapabilities.ITEM_HANDLER ? optional.cast() : super.getCapability(cap);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        optional.invalidate();
    }

    @Override
    public void setRemoved() {
        optional.invalidate();
        super.setRemoved();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MachineCoreTile tile) {
        if (level.isClientSide()) return;
        // 20tickに1回、インベントリに丸石を追加する
        if (level.getGameTime() % 20 == 0) {
            ItemStack item = tile.getItem();
            if (item.isEmpty()) {
                tile.setItem(new ItemStack(Items.COBBLESTONE));
            } else {
                item.grow(1);
            }
        }
    }

    public ItemStackHandler getInventory() {
        return this.inventory;
    }

    public ItemStack getItem() {
        return this.inventory.getStackInSlot(0);
    }

    public void setItem(ItemStack item) {
        this.inventory.setStackInSlot(0, item);
    }

    public LazyOptional<IItemHandler> getOptional() {
        return this.optional;
    }
}
