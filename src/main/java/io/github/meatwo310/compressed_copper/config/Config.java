package io.github.meatwo310.compressed_copper.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class Config {
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final ForgeConfigSpec.IntValue MACHINE_CORE_MAX_PROCESSING_BUFFER = BUILDER
            .comment("The maximum amount of stacks that can be processed at once by the machine core.")
            .defineInRange("machine_core_max_processing_buffer", 100, 4, Integer.MAX_VALUE);

    public static final ForgeConfigSpec SPEC = BUILDER.build();
}
