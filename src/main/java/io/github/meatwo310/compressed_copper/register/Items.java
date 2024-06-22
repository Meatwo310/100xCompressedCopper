package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

public class Items {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CompressedCopper.MODID);
    public static final HashMap<String, RegistryObject<Item>> ITEM_MAP = new HashMap<>();

    static {
        add("compressed_copper",
                () -> new Item(new Item.Properties())
        );
    }

    private static void add(String name, Supplier<Item> itemSupplier) {
        ITEM_MAP.put(name, ITEMS.register(name, itemSupplier));
    }

    public static void addBlockItem(String name, Supplier<BlockItem> blockItemSupplier) {
        ITEM_MAP.put(name, ITEMS.register(name, blockItemSupplier));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
