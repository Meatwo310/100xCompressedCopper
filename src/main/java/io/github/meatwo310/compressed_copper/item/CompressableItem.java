package io.github.meatwo310.compressed_copper.item;

import com.mojang.logging.LogUtils;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.List;

@ParametersAreNonnullByDefault
public class CompressableItem extends Item {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static int MIN_COMPRESSED_LEVEL = 0;
    public static int MAX_COMPRESSED_LEVEL = 100;
    public static List<Integer> WIDTHS = List.of(
            1, 2, 3, 4, 5, 7, 8, 9, 10, 11, 13, 0, 0, 0, 0, 0
    );
    public static List<Integer> COLORS = List.of(
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
    public static final String COMPRESSED_LEVEL_TAG = "CompressedLevel";

    public CompressableItem(Properties properties) {
        super(properties);
    }

    public static boolean hasCompressedLevel(ItemStack itemStack) {
        return itemStack.getTag() != null && itemStack.getTag().contains(COMPRESSED_LEVEL_TAG);
    }

    public static int getCompressedLevel(ItemStack itemStack) {
        return itemStack.getOrCreateTag().getInt(COMPRESSED_LEVEL_TAG);
    }

    public static ItemStack setCompressedLevel(Item item, int compressedLevel) {
        return setCompressedLevel(new ItemStack(item), compressedLevel);
    }

    @Override public static ItemStack setCompressedLevel(ItemStack itemStack, int compressedLevel) {
        itemStack.getOrCreateTag().putInt(COMPRESSED_LEVEL_TAG, compressedLevel);
        return itemStack;
    }

    @Override
    public int getBarWidth(ItemStack itemStack) {
        return WIDTHS.get((getCompressedLevel(itemStack)) / 10);
    }

    @Override
    public int getBarColor(ItemStack itemStack) {
        try {
            return COLORS.get((getCompressedLevel(itemStack)) % 10);
        } catch (IndexOutOfBoundsException e) {
            LOGGER.error("Compressed level out of bounds: {}", getCompressedLevel(itemStack));
            return 0xFF0000;
        }
    }

    @Override
    public boolean isBarVisible(ItemStack itemStack) {
        return hasCompressedLevel(itemStack);
    }
}
