package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.Tags;
import net.thebrewingminer.glowlights.block.copper.utils.ICopperCampfireVariant;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

import static net.thebrewingminer.glowlights.block.copper.WaxUtils.triggerOnHoneycomb;
import static net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils.hasAshWhenUnlit;
import static net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils.isUnlit;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class CopperGlowCampfireBlock extends WaxedCopperGlowCampfireBlock implements IWeatheringCopper, ICopperCampfireVariant {
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

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean survivalMode = !(player.isCreative());
        boolean coalsPresent = hasAshWhenUnlit(state);

        if (heldItem.is(Items.HONEYCOMB)){
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

        if (level.isClientSide()) return InteractionResult.PASS;

        if (isUnlit(state)){
            // If the campfire is NOT lit.
            if (coalsPresent){ // If the coals are still in the campfire

                // Handle lighting when appropriate.
                if ((heldItem.is(Items.FLINT_AND_STEEL) || heldItem.is(Items.FIRE_CHARGE)) && state.getValue(HAS_ASH_UNLIT)) return handleLightingCampfire(level, player, playerHand, heldItem, state, pos, survivalMode);

                // Handle "cleaning" the coals from the campfire.
                if (heldItem.is(Tags.Items.TOOLS_SHOVELS)) return handleCleaningCampfire(level, player, playerHand, heldItem, state, pos, survivalMode);
            } else {
                // If coals are not still in the campfire
                if (heldItem.is(ItemTags.COALS)) return handleRefuelingCampfire(level, state, pos, heldItem, survivalMode); // Handle refueling.
            }
        } else { // Handle interactions with a LIT campfire.
            if (heldItem.is(Tags.Items.TOOLS_SHOVELS)) return handleDowsing(level, player, playerHand, heldItem, state, pos, survivalMode);
        }

        // Handle actions for campfire recipes (Vanilla).
        return handleCampfireRecipe(level, player, heldItem, blockEntity);
    }
}
