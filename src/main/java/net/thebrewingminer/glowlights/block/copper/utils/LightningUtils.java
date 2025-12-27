package net.thebrewingminer.glowlights.block.copper.utils;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public final class LightningUtils {
    private LightningUtils() {}

    public static void randomWalkCleaningCustomCopper(Level level, BlockPos origin, BlockPos.MutableBlockPos cursor, int steps) {
        cursor.set(origin);

        for (int i = 0; i < steps; i++) {
            Optional<BlockPos> next = randomStepCleaningCustomCopper(level, cursor);
            if (next.isEmpty()) return;
            cursor.set(next.get());
        }
    }

    public static Optional<BlockPos> randomStepCleaningCustomCopper(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.randomInCube(level.random, 10, pos, 1)) {
            BlockState state = level.getBlockState(candidate);

            if (state.getBlock() instanceof IWeatheringCopper) {
                IWeatheringCopper.getPrevious(state).ifPresent(prev -> level.setBlockAndUpdate(candidate, prev));
                level.levelEvent(3002, candidate, -1);
                return Optional.of(candidate);
            }
        }

        return Optional.empty();
    }
}
