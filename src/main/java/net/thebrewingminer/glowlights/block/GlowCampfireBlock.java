package net.thebrewingminer.glowlights.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.thebrewingminer.glowlights.block.entity.GlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.init.ModBlockEntities;
import net.thebrewingminer.glowlights.init.ModParticles;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class GlowCampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;
    public static final BooleanProperty SIGNAL_FIRE;
    public static final BooleanProperty HAS_ASH_UNLIT;
    public static final DirectionProperty FACING;
    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final VoxelShape VIRTUAL_FENCE_POST;

    public final float fireDamage;
    public final float fireDamageDelay;

    public static final int WATERLOGGED_PARTICLE_DELAY = 6;
    public static final int DRY_PARTICLE_DELAY = 10;

    public static final int WATERLOGGED_SOUND_DELAY = 75;
    public static final int DRY_SOUND_DELAY = 32;

    static {
        LIT = BlockStateProperties.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        SIGNAL_FIRE = BlockStateProperties.SIGNAL_FIRE;
        HAS_ASH_UNLIT = BooleanProperty.create("has_ash_unlit");
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    public GlowCampfireBlock(BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties);
        this.fireDamage = fireDamage;
        this.fireDamageDelay = fireDamageDelay;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(SIGNAL_FIRE, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(LIT, WATERLOGGED, FACING, SIGNAL_FIRE, HAS_ASH_UNLIT);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext){
        LevelAccessor levelAccessor = blockPlaceContext.getLevel();
        BlockPos pos = blockPlaceContext.getClickedPos();
        boolean inWater = levelAccessor.getFluidState(pos).getType() == Fluids.WATER;
        return ( this.defaultBlockState().setValue(WATERLOGGED, inWater).setValue(LIT, false).setValue(FACING, blockPlaceContext.getHorizontalDirection()).setValue(SIGNAL_FIRE, this.isSmokeSource(levelAccessor.getBlockState(pos.below()))) );
    }

    public BlockState updateShape(BlockState blockState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return super.updateShape(blockState, facing, facingState, level, currentPos, facingPos);
    }

    @Override
    public boolean isPathfindable(BlockState state, BlockGetter getter, BlockPos pos, PathComputationType type) {
        return false;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    public static boolean isLit(BlockState state){
        return state.getValue(LIT);
    }

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        int lightLevel = 0;
        if (isLit(state)){ lightLevel = (isWaterlogged(state)) ? 15 : 10; }

        return lightLevel;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean survivalMode = !(player.isCreative());

        RandomSource randomSource = level.getRandom();

        // Messily handle interactions related to tools.
        if (!isLit(state)){
            if (heldItem.is(Items.FLINT_AND_STEEL) || heldItem.is(Items.FIRE_CHARGE)){
                level.setBlock(pos, state.setValue(LIT, true), 3);
               if (!level.isClientSide()){
                   if (heldItem.is(Items.FLINT_AND_STEEL)){
                       level.playSound(player, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
                       if (survivalMode) heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand));
                   } else {
                       level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2F + 1.0F);
                       if (survivalMode) heldItem.shrink(1);
                   }
                   player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                   return InteractionResult.SUCCESS;
               }
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (heldItem.is(Tags.Items.TOOLS_SHOVELS)){
                if (!level.isClientSide()){
                    level.setBlock(pos, state.setValue(LIT, false), 3);
                    if (isWaterlogged(state)){
                        level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F, 0.6F);
                    } else {
                        level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.4F, 0.25F);
                    }
                    dowse(player, level, pos, state);
                    if (survivalMode){ heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand)); }

                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }

        // Handle actions for campfire recipes (Vanilla).
        if (blockEntity instanceof GlowCampfireBlockEntity glowCampfireBlockEntity) {
            Optional<CampfireCookingRecipe> recipe = glowCampfireBlockEntity.getCookableRecipe(heldItem);
            if (recipe.isPresent()) {
                if (!level.isClientSide() && glowCampfireBlockEntity.placeFood(player, player.getAbilities().instabuild ? heldItem.copy() : heldItem, recipe.get().getCookingTime())) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }

                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.PASS;
    }

    public static void addAmbientGlowParticle(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0) {
            level.addParticle(ParticleTypes.GLOW, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, randomSource.nextFloat() / 2.0F, 5.0E-5, randomSource.nextFloat() / 2.0F);
        }
    }

    public static void playAmbientSound(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0){
            level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource){
        if (!(blockState.getValue(LIT))) return;

        if (blockState.getValue(WATERLOGGED)){
            addAmbientGlowParticle(level, pos, randomSource, WATERLOGGED_PARTICLE_DELAY);
            playAmbientSound(level, pos, randomSource, WATERLOGGED_SOUND_DELAY);
        } else {
            addAmbientGlowParticle(level, pos, randomSource, DRY_PARTICLE_DELAY);
            playAmbientSound(level, pos, randomSource, DRY_SOUND_DELAY);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        if (state.getValue(LIT) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            if (state.getValue(WATERLOGGED)){
                entity.hurt(DamageSource.IN_FIRE, this.fireDamage * 2.5f);
            } else {
                entity.hurt(DamageSource.IN_FIRE, this.fireDamage);
            }
        }

        super.entityInside(state, level, pos, entity);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState blockState) {
        return new GlowCampfireBlockEntity(pos, blockState);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.is(newState.getBlock())) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof GlowCampfireBlockEntity) {
                Containers.dropContents(level, pos, ((GlowCampfireBlockEntity) blockEntity).getItems());
            }

            super.onRemove(state, level, pos, newState, isMoving);
        }
    }

    protected boolean isSmokeSource(BlockState pState) {
        return pState.is(Blocks.HAY_BLOCK);
    }

    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        BlockPos blockpos = blockHitResult.getBlockPos();
        if (!level.isClientSide() && projectile.isOnFire() && projectile.mayInteract(level, blockpos) && !isLit(blockState)) {
            level.setBlock(blockpos, blockState.setValue(BlockStateProperties.LIT, true), 11);
        }
    }

    public static void makeParticles(Level level, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        RandomSource randomsource = level.getRandom();
        SimpleParticleType simpleParticleType = isSignalFire ? ModParticles.SIGNAL_GLOW_SMOKE.get() : ModParticles.COZY_GLOW_SMOKE.get();
        level.addAlwaysVisibleParticle(simpleParticleType, true, (double)pos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1), (double)pos.getY() + randomsource.nextDouble() + randomsource.nextDouble(), (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (spawnExtraSmoke) {
            level.addParticle(ParticleTypes.GLOW_SQUID_INK, (double)pos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    public static void dowse(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos pos, BlockState state) {
        if (levelAccessor.isClientSide()){
            for (int i = 0; i < 20; ++i){
                makeParticles((Level)levelAccessor, pos, state.getValue(SIGNAL_FIRE), true);
            }
        }

        BlockEntity blockEntity = levelAccessor.getBlockEntity(pos);
        if (blockEntity instanceof GlowCampfireBlockEntity glowCampfireBlockEntity){
            glowCampfireBlockEntity.dowse();
        }

        levelAccessor.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::particleTick) : null;
        } else {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cookTick) : createTickerHelper(blockEntityType, ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireBlockEntity::cooldownTick);
        }
    }
}