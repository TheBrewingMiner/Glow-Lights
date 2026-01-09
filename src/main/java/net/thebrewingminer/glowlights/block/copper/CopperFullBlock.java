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

import static net.thebrewingminer.glowlights.block.copper.WaxUtils.triggerOnHoneycomb;

public class CopperFullBlock extends Block implements IWeatheringCopper {
    private final WeatheringCopper.WeatherState weatherState;

    public CopperFullBlock(WeatheringCopper.WeatherState pWeatherState, BlockBehaviour.Properties pProperties) {
        super(pProperties);
        this.weatherState = pWeatherState;
    }

    @Override
    public void randomTick(BlockState pState, ServerLevel pLevel, BlockPos pPos, RandomSource pRandom) {
        this.onRandomTick(pState, pLevel, pPos, pRandom);
    }

    @Override
    public boolean isRandomlyTicking(BlockState pState) {
        return IWeatheringCopper.getNext(pState.getBlock()).isPresent();
    }

    public WeatheringCopper.WeatherState getAge() {
        return this.weatherState;
    }

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
