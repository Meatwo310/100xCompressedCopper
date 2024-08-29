package io.github.meatwo310.compressed_copper.item;

import com.mojang.logging.LogUtils;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.extensions.IForgeItem;
import org.slf4j.Logger;

import java.util.List;

public interface ICompressableItem extends FeatureElement, ItemLike, IForgeItem {
    Logger LOGGER = LogUtils.getLogger();
    int MIN_COMPRESSED_LEVEL = 0;
    int MAX_COMPRESSED_LEVEL = 100;
    List<Integer> WIDTHS = List.of(
            1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 0, 0, 0, 0, 0
    );
    List<Integer> COLORS = List.of(
            0xA55A3F,
            0xA5943F,
            0x78A53F,
            0x46AF4A,
            0x4BBF95,
            0x3F8DA5,
            0x4B5EBF,
            0x7D4BBF,
            0xA53FA2,
            0x98375D,
            0xFF0000,
            0xFF0000,
            0xFF0000,
            0xFF0000,
            0xFF0000,
            0xFF0000
    );
    String COMPRESSED_LEVEL_TAG = "CompressedLevel";

    static boolean hasCompressedLevel(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().contains(COMPRESSED_LEVEL_TAG);
    }

    static int getCompressedLevel(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt(COMPRESSED_LEVEL_TAG);
    }

    static ItemStack setCompressedLevel(Item item, int compressedLevel) {
        return setCompressedLevel(new ItemStack(item), compressedLevel);
    }

    static ItemStack setCompressedLevel(ItemStack itemStack, int compressedLevel) {
        itemStack.getOrCreateTag().putInt(COMPRESSED_LEVEL_TAG, compressedLevel);
        return itemStack;
    }

    default int getBarWidth(ItemStack itemStack) {
        return WIDTHS.get((getCompressedLevel(itemStack)) / 10);
    }

    default int getBarColor(ItemStack itemStack) {
        try {
            return COLORS.get((getCompressedLevel(itemStack)) % 10);
        } catch (IndexOutOfBoundsException e) {
            ICompressableItem.LOGGER.error("Compressed level out of bounds: {}", getCompressedLevel(itemStack));
            return 0xFF0000;
        }
    }

    default boolean isBarVisible(ItemStack itemStack) {
        return hasCompressedLevel(itemStack);
    }
}
