package net.thebrewingminer.glowlights.block.copper;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.thebrewingminer.glowlights.block.AbstractGlowCampfireBlock;
import net.thebrewingminer.glowlights.block.copper.utils.ICopperCampfireVariant;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.init.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils.*;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

public class WaxedCopperGlowCampfireBlock extends AbstractGlowCampfireBlock implements SimpleWaterloggedBlock, ICopperCampfireVariant {
    public static final MapCodec<WaxedCopperGlowCampfireBlock> CODEC = RecordCodecBuilder.mapCodec((instance) ->
        instance.group(
            propertiesCodec(),
            Codec.FLOAT.fieldOf("fire_damage").forGetter((block) -> block.fireDamage),
            Codec.FLOAT.fieldOf("fire_damage_delay").forGetter((block) -> block.fireDamageDelay)
        ).apply(instance, WaxedCopperGlowCampfireBlock::new)
    );

    public WaxedCopperGlowCampfireBlock(BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties, fireDamage, fireDamageDelay);
    }

    @Override
    public MapCodec<? extends WaxedCopperGlowCampfireBlock> codec() {
        return CODEC;
    }

    // Handles campfire recipes just as Vanilla does.
    public ItemInteractionResult handleCampfireRecipe(Level level, Player player, ItemStack heldItem, BlockEntity blockEntity){
        if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) {
            Optional<RecipeHolder<CampfireCookingRecipe>> recipe = copperGlowCampfireBlockEntity.getCookableRecipe(heldItem);
            if (recipe.isPresent()) {
                if (!level.isClientSide() && copperGlowCampfireBlockEntity.placeFood(player, player.getAbilities().instabuild ? heldItem.copy() : heldItem, recipe.get().value().getCookingTime())) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Fills out tool interaction with sounds, sets the block's new state, and calls the block's dowse method.
    public ItemInteractionResult handleDowsing(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode){
        level.setBlock(pos, GlowCampfireUtils.extinguishFlame(state), 3);

        if (isWaterlogged(state)){
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F, 1.0F);
        } else {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.3F, 1.0F);
        }

        dowse(player, level, pos, state);
        if (survivalMode){ heldItem.hurtAndBreak(1, player, heldItem.getEquipmentSlot()); }

        return ItemInteractionResult.SUCCESS;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new CopperGlowCampfireBlockEntity(pos, blockState);
    }

    // For copper campfires, it is necessary to ensure nothing happens in the case of a change to another copper campfire,
    // as oxidation, scraping, and waxing events change the block. This preserves the block entity across blockstate changes
    // and allows a degree of continuity/permanence. May still work on in-game setblock command.
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Block newBlock = newState.getBlock();

        if (newBlock instanceof ICopperCampfireVariant){
            // Short-circuit if block oxidized or was waxed <-> unwaxed.
            return;
        }

        // Handle as usual if the block changed to anything else.
        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    protected void onBlockEntityRemoved(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) {
            Containers.dropContents(level, pos, copperGlowCampfireBlockEntity.getItems());
        }
    }

    // Makes a batch of particles along with glow squid ink particles upon dowsing a flame.
    // Also communicates the dowsing through the block entity.
    public void dowse(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor.isClientSide()){
            for (int i = 0; i < 20; ++i){
                makeParticles((Level)levelAccessor, pos, state.getValue(SIGNAL_FIRE), true);
            }
        }

        BlockEntity blockEntity = levelAccessor.getBlockEntity(pos);
        if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) copperGlowCampfireBlockEntity.dowse();
        levelAccessor.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    // Choose the ticking method appropriate for the client, server, and block state.
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        boolean lit = isLit(blockState);

        if (level.isClientSide) {
            return lit ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::particleTick) : null;
        } else {
            return lit ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::cookTick) : createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::cooldownTick);
        }
    }
}
