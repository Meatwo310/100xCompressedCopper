package io.github.meatwo310.compressed_copper.menu;

import io.github.meatwo310.compressed_copper.blockentity.MachineCoreBlockEntity;
import io.github.meatwo310.compressed_copper.register.Blocks;
import io.github.meatwo310.compressed_copper.register.Menus;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.SlotItemHandler;
import org.jetbrains.annotations.NotNull;

public class MachineCoreMenu extends AbstractContainerMenu {
    private final MachineCoreBlockEntity machineCoreBlockEntity;
    private final ContainerLevelAccess containerLevelAccess;
    private final ContainerData containerData;

    // Client
    public MachineCoreMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, playerInventory.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(2));
    }

    // Server
    public MachineCoreMenu(int id, Inventory playerInventory, BlockEntity blockEntity, ContainerData containerData) {
        super(Menus.MACHINE_CORE_MENU.get(), id);
        if (blockEntity instanceof MachineCoreBlockEntity be) this.machineCoreBlockEntity = be;
        else throw new IllegalStateException("MachineCoreMenu: BlockEntity is not an instance of MachineCoreTile");

        this.containerLevelAccess = ContainerLevelAccess.create(blockEntity.getLevel(), blockEntity.getBlockPos());
        this.containerData = containerData;

        createPlayerHotbar(playerInventory);
        createPlayerInventory(playerInventory);
        createMachineCoreSlots(be);

        addDataSlots(containerData);
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

    private void createMachineCoreSlots(MachineCoreBlockEntity blockEntity) {
        blockEntity.moduleLazyOptional.ifPresent(inventory ->
                this.addSlot(new SlotItemHandler(inventory, 0, 8, 54))
        );
        blockEntity.upgradeLazyOptional.ifPresent(inventory -> {
            for (int i = 0; i < 3; i++) {
                this.addSlot(new SlotItemHandler(inventory, i, 152, 18 + i * 18));
            }
        });
        blockEntity.inputLazyOptional.ifPresent(inventory -> {
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(inventory, i, 35 + (i % 2) * 18, 18 + (i / 2) * 18));
            }
        });
        blockEntity.outputLazyOptional.ifPresent(inventory -> {
            for (int i = 0; i < 4; i++) {
                this.addSlot(new SlotItemHandler(inventory, i, 107 + (i % 2) * 18, 18 + (i / 2) * 18));
            }
        });
    }

    public int getProcessingProgress(int maxSize) {
        int progress = this.containerData.get(MachineCoreBlockEntity.DATA_PROGRESS);
        int maxProgress = this.containerData.get(MachineCoreBlockEntity.DATA_MAX_PROGRESS);

        return maxProgress == 0 ? 0 : progress * maxSize / maxProgress;
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
            // プレイヤーインベントリのアイテムをマシンコアに移動

            // マシンコアのスロットに移動(outputスロット4枠を除く)
            if (!this.moveItemStackTo(itemStack, 36, this.slots.size() - 4, false)) {
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
        return stillValid(this.containerLevelAccess, player, Blocks.MACHINE_CORE.get());
    }
}
