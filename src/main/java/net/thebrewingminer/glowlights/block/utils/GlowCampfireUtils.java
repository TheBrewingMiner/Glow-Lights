package net.thebrewingminer.glowlights.block.utils;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static net.thebrewingminer.glowlights.block.utils.BlockStateProperty.HAS_ASH_UNLIT;

public final class GlowCampfireUtils {
    private GlowCampfireUtils(){}

    // Check if the passed-in state is found in the campfires tag
    // and if it has properties of Glow Campfires.
    // If so, then check if it is eligible to be lit.
    public static boolean canLight(BlockState blockState) {
        if (!blockState.is(BlockTags.CAMPFIRES)) return false;
        if (!blockState.hasProperty(HAS_ASH_UNLIT) || !blockState.hasProperty(LIT)) return false;
        return (blockState.getValue(HAS_ASH_UNLIT) && !blockState.getValue(LIT));
    }
}
