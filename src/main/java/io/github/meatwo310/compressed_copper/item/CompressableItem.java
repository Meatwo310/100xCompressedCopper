package io.github.meatwo310.compressed_copper.item;

import net.minecraft.world.item.Item;

import javax.annotation.ParametersAreNonnullByDefault;

@ParametersAreNonnullByDefault
public class CompressableItem extends Item implements ICompressableItem {
    public CompressableItem(Properties properties) {
        super(properties);
    }
}
