package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final List<RegistryObject<Item>> basicItems = new ArrayList<>();
    private static final List<RegistryObject<Block>> basicBlocks = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput packOutput, ExistingFileHelper efh) {
        generator.addProvider(run, new ItemModel(packOutput, CompressedCopper.MODID, efh));
    }

    public static void addBasicItem(RegistryObject<Item> item) {
        basicItems.add(item);
    }

    public static void addBasicBlock(RegistryObject<Block> block) {
        basicBlocks.add(block);
    }

    private static class ItemModel extends ItemModelProvider {
        public ItemModel(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            basicItems.forEach(item -> {
                try {
                    this.basicItem(item.get());
                } catch (Exception e) {
                    CompressedCopper.LOGGER.error("Failed to generate model for item: {}", item.getId().getPath());
                    CompressedCopper.LOGGER.error(e.getMessage());
                }
            });
            basicBlocks.forEach(block -> {
                try {
                    this.withExistingParent(block.getId().getPath(), modLoc("block/" + block.getId().getPath()));
                } catch (Exception e) {
                    CompressedCopper.LOGGER.error("Failed to generate model for block: {}", block.getId().getPath());
                    CompressedCopper.LOGGER.error(e.getMessage());
                }
            });
        }
    }
}
