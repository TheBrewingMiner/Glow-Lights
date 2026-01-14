package net.thebrewingminer.glowlights.block;

import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.BlockState;

import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

public class GlowLantern extends LanternBlock implements SimpleWaterloggedBlock {

    // Superclass (Vanilla lantern) handles all logic.
    // This custom class is not required to implement the block itself.
    public GlowLantern(Properties pProperties) {
        super(pProperties);
    }

    // The light level behavior expected of this block.
    // Method reference is supplied to the block property call for light level in ModBlocks.
    public static int getLightLevel(BlockState state){
        return (isWaterlogged(state)) ? 15 : 10;
    }
}
