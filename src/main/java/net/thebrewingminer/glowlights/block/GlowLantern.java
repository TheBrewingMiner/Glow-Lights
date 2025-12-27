package net.thebrewingminer.glowlights.block;

import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

public class GlowLantern extends LanternBlock implements SimpleWaterloggedBlock {
    public GlowLantern(Properties pProperties) {
        super(pProperties);
    }

    public static int getLightLevel(BlockState state){
        return (isWaterlogged(state)) ? 15 : 10;
    }
}
