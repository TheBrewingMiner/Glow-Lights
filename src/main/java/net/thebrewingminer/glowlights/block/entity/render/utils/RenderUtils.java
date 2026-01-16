package net.thebrewingminer.glowlights.block.entity.render.utils;

public final class RenderUtils {
    private RenderUtils(){}

    // Make value unique and diffuse it from any structural bias.
    public static long mixSeed(long seed) {
        seed ^= (seed >>> 33);
        seed *= 0xff51afd7ed558ccdL;
        seed ^= (seed >>> 33);
        return seed;
    }

    // Normalize value into float in [0, 1].
    public static float unitFloat(long seed, int bitOffset) {
        return (((seed >>> bitOffset) & 0xFFFFL) / (float) 0x10000);
    }
}
