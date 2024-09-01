package io.github.meatwo310.compressed_copper.recipe;

import io.github.meatwo310.compressed_copper.handler.fluid.FluidsInputHandler;
import io.github.meatwo310.compressed_copper.handler.fluid.FluidsOutputHandler;
import io.github.meatwo310.compressed_copper.handler.item.*;

public record MachineData(
        ItemStackInputHandler input,
        ItemStackOutputHandler output,
        ModuleHandler module,
        UpgradeHandler upgrade,
        ProcessingHandler processingInput,
        ProcessingHandler processingOutput,
        FluidsInputHandler fluidInput,
        FluidsOutputHandler fluidOutput
) {
}
