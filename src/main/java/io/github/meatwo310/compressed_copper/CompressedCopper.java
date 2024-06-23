package io.github.meatwo310.compressed_copper;

import com.mojang.logging.LogUtils;
import io.github.meatwo310.compressed_copper.config.Config;
import io.github.meatwo310.compressed_copper.register.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CompressedCopper.MODID)
public class CompressedCopper {
    public static final String MODID = "compressed_copper";
    public static final Logger LOGGER = LogUtils.getLogger();

    public CompressedCopper() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        Blocks.register(modEventBus);
        TileEntities.register(modEventBus);
        Items.register(modEventBus);
        CreativeModeTabs.register(modEventBus);
        Menus.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
