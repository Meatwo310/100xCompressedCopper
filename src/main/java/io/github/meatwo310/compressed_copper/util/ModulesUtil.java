package io.github.meatwo310.compressed_copper.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class ModulesUtil {
    @Nullable
    public static ResourceLocation getModuleBlockLoc(ItemStack itemStack) {
        if (itemStack.isEmpty()) return null;
        ResourceLocation registryName = ForgeRegistries.ITEMS.getKey(itemStack.getItem());
        if (registryName == null) return null;
        return new ResourceLocation(registryName + "_block");
    }
}
