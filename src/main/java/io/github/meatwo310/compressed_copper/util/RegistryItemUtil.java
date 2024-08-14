package io.github.meatwo310.compressed_copper.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class RegistryItemUtil {
    public static Item getRegistryItem(String name) {
        var item = RegistryObject.create(new ResourceLocation(name), ForgeRegistries.ITEMS);
        return item.get();
    }

    public static ItemStack getRegistryItemStack(String name) {
        return new ItemStack(getRegistryItem(name));
    }

    public static ItemStack getRegistryItemStack(String name, int count) {
        return new ItemStack(getRegistryItem(name), count);
    }
}
