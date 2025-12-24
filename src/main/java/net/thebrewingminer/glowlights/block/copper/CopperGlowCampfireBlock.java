package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

public class CopperGlowCampfireBlock extends WaxedCopperGlowCampfireBlock implements IWeatheringCopper {
    public static final int SUBMERGED_OXIDATION_FACTOR = 7;
    private final WeatheringCopper.WeatherState weatherState;

    public CopperGlowCampfireBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties, fireDamage, fireDamageDelay);
        this.weatherState = weatherState;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(SIGNAL_FIRE, false).setValue(HAS_ASH_UNLIT, true));
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (isWaterlogged(state)){
            if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) this.onRandomTick(state, level, pos, randomSource);
        } else this.onRandomTick(state, level, pos, randomSource);

    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }
}
