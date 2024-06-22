package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class CreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CompressedCopper.MODID);
    public static final String COMPRESSED_COPPER_TAB_ID = "item_group." + CompressedCopper.MODID + ".general";
    public static final RegistryObject<CreativeModeTab> COMPRESSED_COPPER_TAB = CREATIVE_MODE_TABS.register(COMPRESSED_COPPER_TAB_ID, () -> CreativeModeTab.builder()
            .title(Component.translatable(COMPRESSED_COPPER_TAB_ID))
            .icon(() -> Items.ITEM_MAP.get("compressed_copper").get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                Items.ITEM_MAP.forEach((name, item) -> output.accept(item.get()));
            })
            .build()
    );

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
