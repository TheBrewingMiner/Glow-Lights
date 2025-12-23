package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.GlowCampfireBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.block.entity.GlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.init.ModBlockEntities;

import javax.annotation.Nullable;

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

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if(isWaterlogged(state)){
            if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) this.onRandomTick(state, level, pos, randomSource);
        } else this.onRandomTick(state, level, pos, randomSource);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new CopperGlowCampfireBlockEntity(pos, blockState);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::particleTick) : null;
        } else {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cookTick) : createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cooldownTick);
        }
    }
}
