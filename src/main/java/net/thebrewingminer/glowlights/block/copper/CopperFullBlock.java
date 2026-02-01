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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

import static net.thebrewingminer.glowlights.block.copper.utils.WaxUtils.triggerOnHoneycomb;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class CopperFullBlock extends Block implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    // This class is meant to only mimic WeatheringCopperFullBlock.
    // It was created as such to ensure consistency with the custom block map and instanceof checks
    // in lightning-copper interaction logic.
    public CopperFullBlock(WeatheringCopper.WeatherState pWeatherState, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.weatherState = pWeatherState;
    }

    // Called by the game to randomly tick the block.
    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.onRandomTick(pState, pLevel, pPos, pRandom);   // Applies copper ticking logic.
    }

    // Called by the game to query if the block should be randomly ticking.
    // In this case, if the copper has another following oxidation state, it should be.
    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return IWeatheringCopper.getNext(pState.getBlock()).isPresent();
    }

    // Returns this object's weather state.
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

            // Apply wax to block.
            level.setBlock(pos, waxed, 3);

            if (survivalMode) { heldItem.shrink(1); }

            // Trigger events
            level.levelEvent(player, 3003, pos, 0);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxed));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }
}
