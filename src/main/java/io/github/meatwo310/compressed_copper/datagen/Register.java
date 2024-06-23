package io.github.meatwo310.compressed_copper.datagen;

import io.github.meatwo310.compressed_copper.CompressedCopper;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = CompressedCopper.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class Register {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
//        ExistingFileHelper efh = event.getExistingFileHelper();
        Language.register(event.includeClient(), generator);
    }
}
