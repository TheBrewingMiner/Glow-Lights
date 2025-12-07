package net.thebrewingminer.glowlights.block;

import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class GlowLantern extends LanternBlock implements SimpleWaterloggedBlock {
    public GlowLantern(Properties pProperties) {
        super(pProperties);
    }

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        return (isWaterlogged(state)) ? 15 : 10;
    }
}
