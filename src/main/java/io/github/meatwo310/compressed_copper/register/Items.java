package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.datagen.Model;
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

    public static final RegistryObject<Item> COMPRESSED_COPPER = add("compressed_copper",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> MACHINE_CASING_1 = add("machine_casing_1",
            () -> new Item(new Item.Properties())
    );
    public static final RegistryObject<Item> TEST_MODULE_1 = add("test_module_1",
            () -> new Item(new Item.Properties())
    );

    private static RegistryObject<Item> add(String name, Supplier<Item> itemSupplier) {
        return add(name, itemSupplier, true);
    }
    private static RegistryObject<Item> add(String name, Supplier<Item> itemSupplier, boolean registerItemModel) {
        RegistryObject<Item> item = ITEMS.register(name, itemSupplier);
        ITEM_MAP.put(name, item);
        if (registerItemModel) Model.addBasicItem(item);
        return item;
    }

    protected static void addBlockItem(String name, Supplier<BlockItem> blockItemSupplier) {
        ITEM_MAP.put(name, ITEMS.register(name, blockItemSupplier));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
