package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
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
            this.addItem(() -> Items.ITEM_MAP.get("compressed_copper").get(), "Compressed Copper");
        }
    }

    public static class JaJp extends LanguageProvider {
        public JaJp(PackOutput output) {
            super(output, CompressedCopper.MODID, "ja_jp");
        }

        @Override
        protected void addTranslations() {
            this.addItem(() -> Items.ITEM_MAP.get("compressed_copper").get(), "圧縮銅");
        }
    }
}