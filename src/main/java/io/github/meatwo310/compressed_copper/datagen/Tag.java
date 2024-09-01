package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.data.ItemTags;
import io.github.meatwo310.compressed_copper.register.Items;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class Tag {
    private static final List<RegistryObject<Block>> mineableWithPickaxe = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper efh) {
        BlockTagGen blockTagGen = generator.addProvider(run, new BlockTagGen(output, lookupProvider, efh));
        generator.addProvider(run, new ItemTagGen(output, lookupProvider, blockTagGen.contentsGetter(), efh));
    }

    public static void addMineableWithPickaxeBlock(RegistryObject<Block> block) {
        mineableWithPickaxe.add(block);
    }

    public static class BlockTagGen extends BlockTagsProvider {
        public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, CompressedCopper.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            mineableWithPickaxe.forEach(block -> this.tag(BlockTags.MINEABLE_WITH_PICKAXE).add(block.get()));
        }
    }

    public static class ItemTagGen extends ItemTagsProvider {
        public ItemTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, CompletableFuture<TagLookup<Block>> lookupBlock, @Nullable ExistingFileHelper efh) {
            super(output, lookupProvider, lookupBlock, CompressedCopper.MODID, efh);
        }

        @Override
        protected void addTags(@NotNull HolderLookup.Provider provider) {
            Items.ITEM_MAP.forEach((name, item) -> {
                if (name.matches(".*_module"))
                    this.tag(ItemTags.MODULES).add(item.get());
                else if (name.matches(".*_upgrade"))
                    this.tag(ItemTags.UPGRADES).add(item.get());
            });
        }
    }
}
