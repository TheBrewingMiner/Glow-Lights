package net.thebrewingminer.glowlights.block;

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
import net.thebrewingminer.glowlights.block.entity.GlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.init.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.Optional;

import static net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils.isLit;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

public class GlowCampfireBlock extends AbstractGlowCampfireBlock implements SimpleWaterloggedBlock {
    public static final MapCodec<GlowCampfireBlock> CODEC = RecordCodecBuilder.mapCodec((instance) ->
        instance.group(
            propertiesCodec(),
            Codec.FLOAT.fieldOf("fire_damage").forGetter((block) -> block.fireDamage),
            Codec.FLOAT.fieldOf("fire_damage_delay").forGetter((block) -> block.fireDamageDelay)
        ).apply(instance, GlowCampfireBlock::new)
    );

    public GlowCampfireBlock(BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties, fireDamage, fireDamageDelay);
    }

    @Override
    public MapCodec<GlowCampfireBlock> codec() {
        return CODEC;
    }

    // Handles campfire recipes just as Vanilla does.
    @Override
    public ItemInteractionResult handleCampfireRecipe(Level level, Player player, ItemStack heldItem, BlockEntity blockEntity){
        if (blockEntity instanceof GlowCampfireBlockEntity glowCampfireBlockEntity) {
            Optional<RecipeHolder<CampfireCookingRecipe>> recipe = glowCampfireBlockEntity.getCookableRecipe(heldItem);
            if (recipe.isPresent()) {
                if (!level.isClientSide() && glowCampfireBlockEntity.placeFood(player, player.getAbilities().instabuild ? heldItem.copy() : heldItem, recipe.get().value().getCookingTime())) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return ItemInteractionResult.SUCCESS;
                }
                return ItemInteractionResult.CONSUME;
            }
        }
        return ItemInteractionResult.PASS_TO_DEFAULT_BLOCK_INTERACTION;
    }

    // Fills out tool interaction with sounds, sets the block's new state, and calls the block's dowse method.
    @Override
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
        return new GlowCampfireBlockEntity(pos, blockState);
    }

    @Override
    protected void onBlockEntityRemoved(Level level, BlockPos pos) {
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof GlowCampfireBlockEntity glowCampfireBlockEntity) {
            Containers.dropContents(level, pos, glowCampfireBlockEntity.getItems());
        }
    }

    // Makes a batch of particles along with glow squid ink particles upon dowsing a flame.
    // Also communicates the dowsing through the block entity.
    @Override
    public void dowse(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor.isClientSide()){
            for (int i = 0; i < 20; ++i){
                makeParticles((Level)levelAccessor, pos, state.getValue(SIGNAL_FIRE), true);
            }
        }

        BlockEntity blockEntity = levelAccessor.getBlockEntity(pos);
        if (blockEntity instanceof GlowCampfireBlockEntity glowCampfireBlockEntity) glowCampfireBlockEntity.dowse();
        levelAccessor.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    // Choose the ticking method appropriate for the client, server, and block state.
    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        boolean lit = isLit(blockState);

        if (level.isClientSide) {
            return lit ? createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::particleTick) : null;
        } else {
            return lit ? createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cookTick) : createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cooldownTick);
        }
    }
}