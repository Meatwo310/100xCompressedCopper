package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.data.ItemTags;
import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class Tag {
    protected static void register(boolean run, DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh) {
        BlockTagGen blockTagGen = generator.addProvider(run, new BlockTagGen(output, lookupProvider, efh));
        generator.addProvider(run, new ItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), efh));
    }

    public static class BlockTagGen extends BlockTagsProvider {
        public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, CompressedCopper.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {

        }
    }

    public static class ItemTagGen extends ItemTagsProvider {
        public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> lookupBlock, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, lookupBlock, CompressedCopper.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            this.tag(ItemTags.COVERS).add(
                    Items.MACHINE_COVER_1.get()
            );
            this.tag(ItemTags.MODULES).add(
                    Items.TEST_MODULE_1.get()
            );
            this.tag(ItemTags.UPGRADES).add(
                    Items.TEST_UPGRADE_1.get()
            );
        }
    }
}
