package io.github.meatwo310.compressed_copper.data;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

public class ItemTags {
    public static final TagKey<Item> COVERS = createTagKey("covers");
    public static final TagKey<Item> MODULES = createTagKey("modules");
    public static final TagKey<Item> UPGRADES = createTagKey("upgrades");

    private static TagKey<Item> createTagKey(String name) {
        return net.minecraft.tags.ItemTags.create(new ResourceLocation(CompressedCopper.MODID, name));
    }
}
