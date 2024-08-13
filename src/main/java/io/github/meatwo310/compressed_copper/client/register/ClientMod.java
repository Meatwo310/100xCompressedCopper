package io.github.meatwo310.compressed_copper.client.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.client.renderer.MachineCoreTileRenderer;
import io.github.meatwo310.compressed_copper.client.screen.MachineCoreMenuScreen;
import io.github.meatwo310.compressed_copper.register.Menus;
import io.github.meatwo310.compressed_copper.register.TileEntities;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = CompressedCopper.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientMod {
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            MenuScreens.register(Menus.MACHINE_CORE_MENU.get(), MachineCoreMenuScreen::new);
            // Todo: Add more
        });
    }

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(TileEntities.MACHINE_CORE.get(), MachineCoreTileRenderer::new);
    }
}
