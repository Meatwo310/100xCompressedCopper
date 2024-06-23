package io.github.meatwo310.compressed_copper.menu;

import io.github.meatwo310.compressed_copper.blockentity.MachineCoreTile;
import io.github.meatwo310.compressed_copper.register.Blocks;
import io.github.meatwo310.compressed_copper.register.Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MachineCoreMenu extends AbstractContainerMenu {
    private final MachineCoreTile machineCoreTile;
    private final ContainerLevelAccess containerLevelAccess;

    // Client
    public MachineCoreMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    // Server
    public MachineCoreMenu(int id, Inventory playerInventory, BlockEntity blockEntity) {
        super(Menus.MACHINE_CORE_MENU.get(), id);
        if (blockEntity instanceof MachineCoreTile be) this.machineCoreTile = be;
        else throw new IllegalStateException("MachineCoreMenu: BlockEntity is not an instance of MachineCoreTile");

        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);
        createMachineCoreSlots(be);
    }

    private void createPlayerHotbar(Inventory playerInventory) {
        for (int column = 0; column < 9; column++) {
            this.addSlot(new Slot(playerInventory, column, 8 + column * 18, 142));
        }
    }

    private void createPlayerInventory(Inventory playerInventory) {
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                this.addSlot(new Slot(playerInventory, column + row * 9 + 9, 8 + column * 18, 84 + row * 18));
            }
        }
    }

    private void createMachineCoreSlots(MachineCoreTile blockEntity) {
        blockEntity.moduleHandlerLazyOptional.ifPresent(inventory -> {
            this.addSlot(new SlotItemHandler(inventory, 0, 80, 36));
            // ToDo: Add more slots
        });
    }


    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int i) {
        Slot slot = this.getSlot(i);
        ItemStack itemStack = slot.getItem();

        if (itemStack.isEmpty()) {
            return ItemStack.EMPTY;
        }

        ItemStack itemStackCopy = itemStack.copy();

        if (i < 36) {
            if (!this.moveItemStackTo(itemStack, 36, this.slots.size(), true)) {
                return ItemStack.EMPTY;
            }
        } else if (!this.moveItemStackTo(itemStack, 0, 36, false)) {
            return ItemStack.EMPTY;
        }

        slot.setChanged();
        slot.onTake(player, itemStack);

        return itemStackCopy;
    }

    @Override
    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.containerLevelAccess, player, Blocks.BLOCK_MAP.get("machine_core").get());
    }
}
