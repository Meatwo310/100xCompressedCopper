package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.register.Blocks;
import io.github.meatwo310.compressed_copper.register.CreativeModeTabs;
import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class Language {
    public static void register(boolean run, DataGenerator generator) {
        generator.addProvider(run, (DataProvider.Factory<EnUs>) EnUs::new);
        generator.addProvider(run, (DataProvider.Factory<JaJp>) JaJp::new);
    }

    public static class EnUs extends LanguageProvider {
        public EnUs(PackOutput output) {
            super(output, CompressedCopper.MODID, "en_us");
        }

        @Override
        protected void addTranslations() {
            add(CreativeModeTabs.COMPRESSED_COPPER_TAB_ID, "Compressed Copper");
            addItem(() -> Items.ITEM_MAP.get("compressed_copper").get(), "Compressed Copper");
            addItem(() -> Items.ITEM_MAP.get("machine_casing_1").get(), "1x Machine Casing");
            addItem(() -> Items.ITEM_MAP.get("test_module_1").get(), "Test Module");
            addBlock(() -> Blocks.BLOCK_MAP.get("machine_core").get(), "Machine Core");
        }
    }

    public static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, CompressedCopper.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            addItem(() -> Items.ITEM_MAP.get("compressed_copper").get(), "圧縮銅");
            addItem(() -> Items.ITEM_MAP.get("machine_casing_1").get(), "1倍マシンケーシング");
            addItem(() -> Items.ITEM_MAP.get("test_module_1").get(), "1倍テストモジュール");
            addBlock(() -> Blocks.BLOCK_MAP.get("machine_core").get(), "マシンコア");
        }
    }
}