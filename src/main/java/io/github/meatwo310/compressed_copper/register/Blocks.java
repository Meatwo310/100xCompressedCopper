package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.block.MachineCore;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.function.Supplier;

public class Blocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CompressedCopper.MODID);
    public static final HashMap<String, RegistryObject<Block>> BLOCK_MAP = new HashMap<>();

    public static final RegistryObject<Block> MACHINE_CORE = add("machine_core",
            () -> new MachineCore(BlockBehaviour.Properties.of()),
            () -> new BlockItem(Blocks.MACHINE_CORE.get(), new BlockItem.Properties())
    );

    private static RegistryObject<Block> add(String name, Supplier<Block> blockSupplier, Supplier<BlockItem> blockItemSupplier) {
        RegistryObject<Block> block = BLOCKS.register(name, blockSupplier);
        BLOCK_MAP.put(name, block);
        Items.addBlockItem(name, blockItemSupplier);
        return block;
    }

    public static void register(IEventBus modEventBus) {
        BLOCKS.register(modEventBus);
    }
}
