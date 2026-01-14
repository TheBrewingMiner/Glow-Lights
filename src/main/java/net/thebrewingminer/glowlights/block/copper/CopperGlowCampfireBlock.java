package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.thebrewingminer.glowlights.block.copper.utils.ICopperCampfireVariant;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

import static net.thebrewingminer.glowlights.block.copper.utils.WaxUtils.triggerOnHoneycomb;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class CopperGlowCampfireBlock extends WaxedCopperGlowCampfireBlock implements IWeatheringCopper, ICopperCampfireVariant {
    public static final int SUBMERGED_OXIDATION_FACTOR = 7;
    private final WeatheringCopper.WeatherState weatherState;

    // WaxedCopperGlowCampfireBlock handles everything the campfire normally does.
    // Stores copper weather state.
    public CopperGlowCampfireBlock(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties, fireDamage, fireDamageDelay);
        this.weatherState = weatherState;
    }

    // Copper-related methods required to implement.
    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    // Called by the game to randomly tick the block.
    // This implementation variates the block's random tick calls with time if WATERLOGGED is true.
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if (isWaterlogged(state)){
            if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) this.onRandomTick(state, level, pos, randomSource);
        } else this.onRandomTick(state, level, pos, randomSource);

    }

    // Checks if the block can oxidize further, and thus is still eligible for ticking.
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    // Handles use of honeycomb to wax the copper block, and otherwise calls
    // the superclass's method to handle everything else as usual.
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        boolean survivalMode = !(player.isCreative());

        if (heldItem.is(Items.HONEYCOMB)){
            return IWeatheringCopper.getWaxed(state).map(waxed -> {

                // Trigger advancement via helper method
                triggerOnHoneycomb(level, player, pos, heldItem);

                // Apply wax on block
                level.setBlock(pos, waxed, 3);

                if (survivalMode) { heldItem.shrink(1); }

                // Trigger events
                level.levelEvent(player, 3003, pos, 0);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxed));

                return InteractionResult.sidedSuccess(level.isClientSide);
            }).orElse(InteractionResult.PASS);
        }

        return super.use(state, level, pos, player, playerHand, hitResult);
    }
}
