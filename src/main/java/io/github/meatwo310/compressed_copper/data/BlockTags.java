package io.github.meatwo310.compressed_copper.data;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;

public class BlockTags {
    private static TagKey<Block> createTagKey(String name) {
        return net.minecraft.tags.BlockTags.create(new ResourceLocation(CompressedCopper.MODID, name));
    }
}
