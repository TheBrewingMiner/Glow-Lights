package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.GlowCampfireBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

public class CopperGlowCampfireBlock extends GlowCampfireBlock implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;
    public static final int SUBMERGED_OXIDATION_FACTOR = 7;

    public CopperGlowCampfireBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties, fireDamage, fireDamageDelay);
        this.weatherState = weatherState;
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if(isWaterlogged(state)){
            if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) this.onRandomTick(state, level, pos, randomSource);
        } else this.onRandomTick(state, level, pos, randomSource);

    }
}
