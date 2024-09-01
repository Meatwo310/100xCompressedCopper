package io.github.meatwo310.compressed_copper.util;

/**
 * Utility class for mathematical operations.
 */
public class MathUtil {
    /**
     * Maps a value from one range to another.
     * Returns an integer value.
     *
     * @param value The value to map.
     * @param fromMin The original range minimum.
     * @param fromMax The original range maximum.
     * @param toMin The target range minimum.
     * @param toMax The target range maximum.
     * @return The mapped value in the target range.
     */
    public static int map(int value, int fromMin, int fromMax, int toMin, int toMax) {
        return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin;
    }
}
