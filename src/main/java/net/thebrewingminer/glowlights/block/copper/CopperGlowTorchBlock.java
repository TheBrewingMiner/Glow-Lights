package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.thebrewingminer.glowlights.block.GlowTorchBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

public class CopperGlowTorchBlock extends GlowTorchBlock implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;
    public static final int SUBMERGED_OXIDATION_FACTOR = 7;

    public CopperGlowTorchBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.weatherState = weatherState;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) {
            this.onRandomTick(state, level, pos, randomSource);
        }
    }

    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        Block block = state.getBlock();
        ItemStack heldItem = player.getItemInHand(playerHand);
        boolean survivalMode = !(player.isCreative());

        if (!(heldItem.is(Items.HONEYCOMB))) return super.use(state, level, pos, player, playerHand, hitResult);

        IWeatheringCopper.getWaxed(block).ifPresent(waxed -> level.setBlock(pos, waxed.withPropertiesOf(state), 3));
        if (survivalMode){ heldItem.shrink(1); }
        level.levelEvent(player, 3003, pos, 0);

        return InteractionResult.sidedSuccess(level.isClientSide);
    }
}
