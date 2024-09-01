package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.compat.jei.JEICompat;
import io.github.meatwo310.compressed_copper.datagen.Model;
import io.github.meatwo310.compressed_copper.item.CompressableItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompressedCopper.MODID);
    public static final Map<String, RegistryObject<Item>> ITEM_MAP = new LinkedHashMap<>();

    public static final RegistryObject<Item> COMPRESSED_COPPER = addCompressable("compressed_copper");
    public static final RegistryObject<Item> TEST_MODULE = addModule("test_module");
    public static final RegistryObject<Item> TEST_UPGRADE = addUpgrade("test_upgrade");


    private static RegistryObject<Item> add(String name, Supplier<Item> itemSupplier) {
        RegistryObject<Item> item = ITEMS.register(name, itemSupplier);
        ITEM_MAP.put(name, item);
        return item;
    }

    private static RegistryObject<Item> addCompressable(String name) {
        return addCompressable(name, () -> new CompressableItem(new Item.Properties()));
    }

    private static RegistryObject<Item> addCompressable(String name, Supplier<Item> itemSupplier) {
        RegistryObject<Item> item = ITEMS.register(name, itemSupplier);
        ITEM_MAP.put(name, item);
        JEICompat.addUseNbt(item);
        Model.addBasicItem(item);
        return item;
    }

    private static RegistryObject<Item> addModule(String name) {
        var item = addCompressable(name);
        Blocks.addModuleBlock(name);
        return item;
    }

    private static RegistryObject<Item> addUpgrade(String name) {
        return addCompressable(name);
    }

    protected static void addBlockItem(String name, Supplier<BlockItem> blockItemSupplier) {
        ITEM_MAP.put(name, ITEMS.register(name, blockItemSupplier));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
