package io.github.meatwo310.compressed_copper.datagen;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class LootTable {
    private static final List<RegistryObject<Block>> dropSelfBlocks = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput output) {
        generator.addProvider(run, new LootTableProvider(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(BlockLootTables::new, LootContextParamSets.BLOCK)
        )));
    }

    public static void addDropSelfBlock(RegistryObject<Block> block) {
        dropSelfBlocks.add(block);
    }

    public static class BlockLootTables extends BlockLootSubProvider {
        public BlockLootTables() {
            super(Collections.emptySet(), FeatureFlags.REGISTRY.allFlags());
        }

        @Override
        protected void generate() {
            dropSelfBlocks.forEach(block -> this.dropSelf(block.get()));
        }

        @Override
        protected @NotNull Iterable<Block> getKnownBlocks() {
            return dropSelfBlocks.stream().map(RegistryObject::get)::iterator;
        }
    }
}
