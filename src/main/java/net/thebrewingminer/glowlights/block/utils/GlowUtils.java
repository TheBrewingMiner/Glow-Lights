package net.thebrewingminer.glowlights.block.utils;

import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.WATERLOGGED;

public final class GlowUtils {
    private GlowUtils(){}

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }
}