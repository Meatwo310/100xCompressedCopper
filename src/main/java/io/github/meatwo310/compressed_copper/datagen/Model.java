package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.core.Direction;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class Model {
    private static final List<RegistryObject<Item>> basicItems = new ArrayList<>();
    private static final List<RegistryObject<Block>> basicBlocks = new ArrayList<>();
    private static final List<RegistryObject<Block>> moduleBlocks = new ArrayList<>();

    protected static void register(boolean run, DataGenerator generator, PackOutput packOutput, ExistingFileHelper efh) {
        generator.addProvider(run, new ItemModel(packOutput, CompressedCopper.MODID, efh));
        generator.addProvider(run, new BlockModel(packOutput, CompressedCopper.MODID, efh));
    }

    public static void addBasicItem(RegistryObject<Item> item) {
        basicItems.add(item);
    }

    public static void addBasicBlock(RegistryObject<Block> block) {
        basicBlocks.add(block);
    }

    public static void addModuleBlock(RegistryObject<Block> block) {
        moduleBlocks.add(block);
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
                    CompressedCopper.LOGGER.error("Failed to generate item model for: {}", item.getId().getPath());
                    CompressedCopper.LOGGER.error(e.getMessage());
                }
            });
            basicBlocks.forEach(block -> {
                try {
                    this.withExistingParent(block.getId().getPath(), modLoc("block/" + block.getId().getPath()));
                } catch (Exception e) {
                    CompressedCopper.LOGGER.error("Failed to generate item model for block: {}", block.getId().getPath());
                    CompressedCopper.LOGGER.error(e.getMessage());
                }
            });
        }
    }

    private static class BlockModel extends BlockModelProvider {
        public BlockModel(PackOutput output, String modid, ExistingFileHelper existingFileHelper) {
            super(output, modid, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            moduleBlocks.forEach(block -> {
                try {
                    this.getBuilder(block.getId().getPath())
                            .parent(this.getExistingFile(mcLoc("block/block")))
                            .texture("0", modLoc("block/module/" + block.getId().getPath()))
                            .element()
                            .from(0, 0, 0)
                            .to(16, 16, 16)
                            .allFaces((direction, faceBuilder) -> {
                                float[] uvs = getUvs(direction);
                                faceBuilder
                                        .uvs(uvs[0], uvs[1], uvs[2], uvs[3])
                                        .texture("#0")
                                        .end();
                            })
                            .end();
                } catch (Exception e) {
                    CompressedCopper.LOGGER.error("Failed to generate block model for module: {}", block.getId().getPath());
                    CompressedCopper.LOGGER.error(e.getMessage());
                }
            });
        }

        private float[] getUvs(Direction direction) {
            return switch (direction) {
                case NORTH -> new float[]{0, 0, 4, 4};
                case EAST -> new float[]{0, 4, 4, 8};
                case SOUTH -> new float[]{4, 0, 8, 4};
                case WEST -> new float[]{4, 4, 8, 8};
                case UP -> new float[]{4, 12, 0, 8};
                case DOWN -> new float[]{12, 0, 8, 4};
            };
        }
    }
}
