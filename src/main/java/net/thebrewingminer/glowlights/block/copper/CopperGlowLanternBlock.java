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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.thebrewingminer.glowlights.block.GlowLanternBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

import static net.thebrewingminer.glowlights.block.copper.utils.WaxUtils.triggerOnHoneycomb;

public class CopperGlowLanternBlock extends GlowLanternBlock implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    // Superclass (GlowLanternBlock) handles most of the object.
    // This object handles copper weather state itself.
    public CopperGlowLanternBlock(WeatheringCopper.WeatherState weatherState, Properties properties){
        super(properties);
        this.weatherState = weatherState;
    }

    // Returns this object's weather state.
    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    // Called by the game to randomly tick the block.
    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        this.onRandomTick(state, level, pos, randomSource);
    }

    // Called by the game to query if the block should be randomly ticking.
    // In this case, if the copper has another following oxidation state, it should be.
    @Override
    public boolean isRandomlyTicking(BlockState state) {
        return IWeatheringCopper.getNext(state.getBlock()).isPresent();
    }

    // Checks if the player is holding honeycomb, and if so, handle waxing logic as a honeycomb would.
    // Otherwise, delegates to the superclass's method (which in this case is a simple return of InteractionResult.PASS).
    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        boolean survivalMode = !(player.isCreative());

        if (!(heldItem.is(Items.HONEYCOMB))) return super.use(state, level, pos, player, playerHand, hitResult);

        return IWeatheringCopper.getWaxed(state).map(waxed -> {

            // Trigger advancement
            triggerOnHoneycomb(level, player, pos, heldItem);

            // Apply wax
            level.setBlock(pos, waxed, 3);

            if (survivalMode) { heldItem.shrink(1); }

            level.levelEvent(player, 3003, pos, 0);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, waxed));

            return InteractionResult.sidedSuccess(level.isClientSide);
        }).orElse(InteractionResult.PASS);
    }
}