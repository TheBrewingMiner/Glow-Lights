package net.thebrewingminer.glowlights.block.utils;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import static net.minecraft.world.level.block.state.properties.BlockStateProperties.LIT;
import static net.thebrewingminer.glowlights.block.utils.BlockStateProperty.HAS_ASH_UNLIT;

public final class GlowCampfireUtils {
    private GlowCampfireUtils(){}

    public static boolean isLit(BlockState state){
        return state.getValue(LIT);
    }

    public static boolean isUnlit(BlockState state){
        return !isLit(state);
    }

    public static boolean hasAshWhileUnlit(BlockState state){
        return state.getValue(HAS_ASH_UNLIT);
    }

    // Check if the passed-in state is found in the campfires tag
    // and if it has properties of Glow Campfires.
    // If so, then check if it is eligible to be lit.
    public static boolean canLight(BlockState blockState) {
        if (!blockState.is(BlockTags.CAMPFIRES)) return false;
        if (!blockState.hasProperty(HAS_ASH_UNLIT) || !blockState.hasProperty(LIT)) return false;
        return (hasAshWhileUnlit(blockState) && isUnlit(blockState));
    }

    // Explicitly check if the blockstate is a glow campfire (Not just eligibility for lighting).
    public static boolean isGlowCampfire(BlockState blockState) {
        return (blockState.is(BlockTags.CAMPFIRES)
                && blockState.hasProperty(BlockStateProperties.LIT)
                && blockState.hasProperty(BlockStateProperty.HAS_ASH_UNLIT));
    }

    public static BlockState litFromAsh(BlockState blockState) {
        return blockState.setValue(BlockStateProperties.LIT, true)
                         .setValue(BlockStateProperty.HAS_ASH_UNLIT, false);
    }

    public static BlockState extinguishFlame(BlockState blockState) {
        return blockState.setValue(BlockStateProperties.LIT, false)
                         .setValue(BlockStateProperty.HAS_ASH_UNLIT, true);
    }

    public static BlockState cleanAsh(BlockState blockState) {
        return blockState.setValue(BlockStateProperty.HAS_ASH_UNLIT, false);
    }

    public static BlockState addCoals(BlockState blockState) {
        return blockState.setValue(BlockStateProperty.HAS_ASH_UNLIT, true);
    }
}