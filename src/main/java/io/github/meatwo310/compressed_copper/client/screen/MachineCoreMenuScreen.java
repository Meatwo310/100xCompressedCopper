package io.github.meatwo310.compressed_copper.client.screen;

import io.github.meatwo310.compressed_copper.menu.MachineCoreMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class MachineCoreMenuScreen extends AbstractContainerScreen<MachineCoreMenu> {
//    private static final ResourceLocation TEXTURE = new ResourceLocation(CompressedCopper.MODID, "textures/gui/machine_core_screen.png");
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft", "textures/gui/container/generic_54.png");

    public MachineCoreMenuScreen(MachineCoreMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics guiGraphics, float tick, int x, int y) {
        renderBackground(guiGraphics);
        guiGraphics.blit(TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int x, int y, float tick) {
        super.render(guiGraphics, x, y, tick);
        renderTooltip(guiGraphics, x, y);
    }
}
