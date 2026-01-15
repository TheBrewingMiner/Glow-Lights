package net.thebrewingminer.glowlights.block.entity.render;

public final class RenderUtils {
    private RenderUtils(){}

    public static float mixSeed(long seed) {
        seed ^= (seed >>> 33);
        seed *= 0xff51afd7ed558ccdL;
        seed ^= (seed >>> 33);
        return (seed & 0xFFFFFF) / (float)0x1000000;
    }

}
