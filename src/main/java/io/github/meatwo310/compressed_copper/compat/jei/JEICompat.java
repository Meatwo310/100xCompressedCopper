package io.github.meatwo310.compressed_copper.compat.jei;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

@JeiPlugin
public class JEICompat implements IModPlugin {
    private static final List<RegistryObject<Item>> USE_NBT = new ArrayList<>();

    public static void addUseNbt(RegistryObject<Item> item) {
        USE_NBT.add(item);
    }

    @NotNull
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CompressedCopper.MODID, "jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration) {
        USE_NBT.stream()
                .map(RegistryObject::get)
                .forEach(registration::useNbtForSubtypes);
    }
}
