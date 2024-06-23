package io.github.meatwo310.compressed_copper.blockentity;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.itemhandler.ModuleHandler;
import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import io.github.meatwo310.compressed_copper.register.TileEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MachineCoreTile extends BlockEntity implements MenuProvider {
    private static final Component TITLE = Component.translatable("container." + CompressedCopper.MODID + ".machine_core");
    public final LazyOptional<IItemHandler> moduleHandlerLazyOptional;
    private final ItemStack module;

    public MachineCoreTile(BlockPos pos, BlockState state) {
        super(TileEntities.MACHINE_CORE.get(), pos, state);
        this.module = ItemStack.EMPTY;
        moduleHandlerLazyOptional = LazyOptional.of(() -> new ModuleHandler(NonNullList.withSize(1, this.module)));
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return moduleHandlerLazyOptional.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        this.moduleHandlerLazyOptional.invalidate();
        super.setRemoved();
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        moduleHandlerLazyOptional.invalidate();
    }

    public static void tick(Level level, BlockPos pos, BlockState state, MachineCoreTile tile) {
        if (level.isClientSide()) return;
        IItemHandler handler = tile.moduleHandlerLazyOptional.orElse(null);
    }

    @NotNull
    @Override
    public Component getDisplayName() {
        return TITLE;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, @NotNull Inventory inventory, @NotNull Player player) {
        return new MachineCoreMenu(id, inventory, this);
    }

    public IItemHandler getModuleHandler() {
        return moduleHandlerLazyOptional.orElse(null);
    }
}
