package io.github.meatwo310.compressed_copper.register;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import io.github.meatwo310.compressed_copper.blockentity.MachineCoreTile;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class TileEntities {
    public static final DeferredRegister<BlockEntityType<?>> TILE_ENTITY_TYPES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CompressedCopper.MODID);
    @SuppressWarnings("DataFlowIssue")
    public static final RegistryObject<BlockEntityType<MachineCoreTile>> MACHINE_CORE =
            TILE_ENTITY_TYPES.register("machine_core",
                () -> BlockEntityType.Builder.of(
                        MachineCoreTile::new,
                        Blocks.BLOCK_MAP.get("machine_core").get()
                ).build(null)
            );

    public static void register(IEventBus modEventBus) {
        TILE_ENTITY_TYPES.register(modEventBus);
    }
}
