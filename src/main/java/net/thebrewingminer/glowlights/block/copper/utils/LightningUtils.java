package net.thebrewingminer.glowlights.block.copper.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;

import java.util.Optional;

import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

public final class LightningUtils {
    private LightningUtils() {}

    // Called to start a "walk" of cleaning copper with the specified pos as the origin and a number of steps.
    public static void randomWalkCleaningCustomCopper(Level level, BlockPos origin, BlockPos.MutableBlockPos cursor, int steps) {
        cursor.set(origin);

        for (int i = 0; i < steps; i++) {
            Optional<BlockPos> next = randomStepCleaningCustomCopper(level, cursor);    // Start cleaning copper here and get a potential next position to start from.
            if (next.isEmpty()) return; // If the position returned Optional.empty(), stop this path.
            cursor.set(next.get());     // Otherwise, set the next position to the returned position for the walk to continue from.
        }
    }

    // Called on the passed in position in the level to check if it or surrounding blocks are copper.
    // If it finds a copper block, it applies deoxidization to it. If the copper block is an unwaxed campfire, it also lights it if eligible.
    // Then, it returns the position of the block it did this to the calling method (above) to start another step.

    // If the block is not copper but is a glow campfire that can be lit AND is waterlogged, it will be lit, however this branch
    // returns empty to stop the random walk altogether.
    public static Optional<BlockPos> randomStepCleaningCustomCopper(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.randomInCube(level.random, 10, pos, 1)) {
            BlockState state = level.getBlockState(candidate);
            Block block = state.getBlock();

            // If this block is an instance of unwaxed copper, mark this position as a candidate.
            if (block instanceof WeatheringCopper) {
                // If this is a custom copper block, apply cleaning logic to it.
                if (block instanceof IWeatheringCopper){
                    Optional<BlockState> prevState = IWeatheringCopper.getPrevious(state);
                    BlockState newState = prevState.orElse(state);

                    // If a copper glow campfire (unwaxed) is "walked" to and is fueled, light it.
                    if (GlowCampfireUtils.isGlowCampfire(state) && GlowCampfireUtils.canLight(state)){
                        newState = GlowCampfireUtils.litFromAsh(newState);
                    }

                    level.setBlockAndUpdate(candidate, newState);
                    level.levelEvent(3002, candidate, -1);
                }

                return Optional.of(candidate);  // Next position for the random walk to step from is this position.
            }

            // Waterlogged lightable glow campfires will be lit if walked into...
            if (GlowCampfireUtils.isGlowCampfire(state) && GlowCampfireUtils.canLight(state)){
                if (isWaterlogged(state)) { level.setBlock(candidate, GlowCampfireUtils.litFromAsh(state), 3); }
            }
        }

        return Optional.empty();    // The walk is stopped at a non-copper block.
    }
}