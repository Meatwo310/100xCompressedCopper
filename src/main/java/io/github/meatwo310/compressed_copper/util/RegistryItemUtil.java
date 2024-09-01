package io.github.meatwo310.compressed_copper.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for registry item operations.
 */
public class RegistryItemUtil {
    /**
     * Retrieves an Item by name.
     * @param name Item name
     * @return Item or null if not found
     */
    @Nullable
    public static Item getRegistryItem(String name) {
        RegistryObject<Item> item = RegistryObject.create(new ResourceLocation(name), ForgeRegistries.ITEMS);
        return item.isPresent() ? item.get() : null;
    }

    /**
     * Retrieves an ItemStack by name.
     * @param name Item name
     * @return ItemStack or null if not found
     */
    @Nullable
    public static ItemStack getRegistryItemStack(String name) {
        return getRegistryItemStack(name, 1);
    }

    /**
     * Retrieves an ItemStack from the registry.
     * @param name Item name
     * @param count Item count
     * @return ItemStack or null if not found
     */
    @Nullable
    public static ItemStack getRegistryItemStack(String name, int count) {
        Item item = getRegistryItem(name);
        return item != null ? new ItemStack(item, count) : null;
    }
}
