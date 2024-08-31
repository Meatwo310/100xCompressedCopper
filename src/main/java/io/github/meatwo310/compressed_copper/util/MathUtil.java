package io.github.meatwo310.compressed_copper.util;

public class MathUtil {
    public static int map(int value, int fromMin, int fromMax, int toMin, int toMax) {
        return (value - fromMin) * (toMax - toMin) / (fromMax - fromMin) + toMin;
    }
}
