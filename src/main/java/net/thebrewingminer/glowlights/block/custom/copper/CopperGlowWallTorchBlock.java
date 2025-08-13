package net.thebrewingminer.glowlights.block.custom.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.custom.GlowWallTorchBlock;
import net.thebrewingminer.glowlights.block.custom.copper.utils.IWeatheringCopper;

public class CopperGlowWallTorchBlock extends GlowWallTorchBlock implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    public CopperGlowWallTorchBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        this.onRandomTick(state, level, pos, randomSource);
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }
}
