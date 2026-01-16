package net.thebrewingminer.glowlights.block.entity.render;

public final class RenderUtils {
    private RenderUtils(){}

    public static long mixSeed(long seed) {
        seed ^= (seed >>> 33);
        seed *= 0xff51afd7ed558ccdL;
        seed ^= (seed >>> 33);
        return seed;
    }

    public static float unitFloat(long seed, int bitOffset) {
        return (((seed >>> bitOffset) & 0xFFFFL) / (float) 0x10000);
    }
}
