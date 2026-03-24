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
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.thebrewingminer.glowlights.block.GlowLantern;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

import static net.thebrewingminer.glowlights.block.copper.utils.WaxUtils.triggerOnHoneycomb;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class CopperGlowLantern extends GlowLantern implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;
    public static final int SUBMERGED_OXIDATION_FACTOR = 7;

    // Superclass (GlowLantern) handles most of the object.
    // This object handles copper weather state itself.
    public CopperGlowLantern(WeatheringCopper.WeatherState weatherState, BlockBehaviour.Properties properties) {
        super(properties);
        this.weatherState = weatherState;
    }

    // Called by the game to randomly tick the block.
    // This implementation variates the block's random tick calls with time if WATERLOGGED is true.
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        if(isWaterlogged(state)){
            if (randomSource.nextInt(SUBMERGED_OXIDATION_FACTOR) == 0) this.changeOverTime(state, level, pos, randomSource);
        } else this.changeOverTime(state, level, pos, randomSource);

    }

    // Called by the game to query if the block should be randomly ticking.
    // In this case, if the copper has another following oxidation state, it should be.
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    // Returns this object's weather state.
    @Override
    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

    // Checks if the player is holding honeycomb, and if so, handle waxing logic as a honeycomb would.
    // Otherwise, delegates to the superclass's method (which in this case is a simple return of InteractionResult.PASS).
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        boolean survivalMode = !(player.isCreative());

        if (!(heldItem.is(Items.HONEYCOMB))) return super.use(state, level, pos, player, playerHand, hitResult);

        return IWeatheringCopper.getWaxed(state).map(waxed -> {

            // Trigger advancement via helper method.
            triggerOnHoneycomb(level, player, pos, heldItem);

            // Apply wax to block
            level.setBlock(pos, waxed, 3);

            if (survivalMode) { heldItem.shrink(1); }

            // Trigger events
            level.levelEvent(player, 3003, pos, 0);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxed));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }
}