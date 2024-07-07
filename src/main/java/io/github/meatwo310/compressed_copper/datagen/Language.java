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
    protected static void register(boolean run, DataGenerator generator) {
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
            add("container." + CompressedCopper.MODID + ".machine_core", "Machine Core");
            addItem(Items.COMPRESSED_COPPER, "Compressed Copper");
            addItem(Items.MACHINE_CASING_1, "1x Machine Casing");
            addItem(Items.TEST_MODULE_1, "Test Module");
            addBlock(Blocks.MACHINE_CORE, "Machine Core");
        }
    }

    public static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, CompressedCopper.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            add("container." + CompressedCopper.MODID + ".machine_core", "マシンコア");
            addItem(Items.COMPRESSED_COPPER, "圧縮銅");
            addItem(Items.MACHINE_CASING_1, "1倍マシンケーシング");
            addItem(Items.TEST_MODULE_1, "1倍テストモジュール");
            addBlock(Blocks.MACHINE_CORE, "マシンコア");
        }
    }
}