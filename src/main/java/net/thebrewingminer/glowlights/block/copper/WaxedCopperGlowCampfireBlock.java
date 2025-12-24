package net.thebrewingminer.glowlights.block.copper;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.ItemTags;
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
import net.thebrewingminer.glowlights.block.copper.utils.ICopperCampfireVariant;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.init.ModBlockEntities;
import net.thebrewingminer.glowlights.init.ModParticles;

import javax.annotation.Nullable;
import java.util.Optional;

public class WaxedCopperGlowCampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock, ICopperCampfireVariant {
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

    public WaxedCopperGlowCampfireBlock(BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties);
        this.fireDamage = fireDamage;
        this.fireDamageDelay = fireDamageDelay;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(SIGNAL_FIRE, false).setValue(HAS_ASH_UNLIT, true));
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

    public static boolean isUnlit(BlockState state){
        return !isLit(state);
    }

    public static boolean hasAshWhenUnlit(BlockState state){
        return state.getValue(HAS_ASH_UNLIT);
    }

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        int lightLevel = 0;
        if (isLit(state)){ lightLevel = (isWaterlogged(state)) ? 15 : 10; }

        return lightLevel;
    }

    public static InteractionResult handleCampfireRecipe(Level level, Player player, ItemStack heldItem, BlockEntity blockEntity){
        if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) {
            Optional<CampfireCookingRecipe> recipe = copperGlowCampfireBlockEntity.getCookableRecipe(heldItem);
            if (recipe.isPresent()) {
                if (!level.isClientSide() && copperGlowCampfireBlockEntity.placeFood(player, player.getAbilities().instabuild ? heldItem.copy() : heldItem, recipe.get().getCookingTime())) {
                    player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                    return InteractionResult.SUCCESS;
                }
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.PASS;
    }

    public InteractionResult handleLightingCampfire(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode){
        RandomSource randomSource = level.getRandom();
        level.setBlock(pos, state.setValue(LIT, true).setValue(HAS_ASH_UNLIT, false), 3);

        if (heldItem.is(Items.FLINT_AND_STEEL)){
            level.playSound(null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, level.getRandom().nextFloat() * 0.4F + 0.8F);
            if (survivalMode) heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand));
        } else {
            level.playSound(null, pos, SoundEvents.FIRECHARGE_USE, SoundSource.BLOCKS, 1.0F, (randomSource.nextFloat() - randomSource.nextFloat()) * 0.2F + 1.0F);
            if (survivalMode) heldItem.shrink(1);
        }

        return InteractionResult.SUCCESS;
    }

    public InteractionResult handleCleaningCampfire(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode){
        level.setBlock(pos, state.setValue(HAS_ASH_UNLIT, false), 3);
        level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (survivalMode) heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand));
        return InteractionResult.SUCCESS;
    }

    public InteractionResult handleRefuelingCampfire(Level level, BlockState state, BlockPos pos, ItemStack heldItem, boolean survivalMode){
        level.setBlock(pos, state.setValue(HAS_ASH_UNLIT, true), 3);
        level.playSound(null, pos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (survivalMode) heldItem.shrink(1);
        return InteractionResult.SUCCESS;
    }

    public InteractionResult handleDowsing(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode){
        level.setBlock(pos, state.setValue(LIT, false).setValue(HAS_ASH_UNLIT, true), 3);

        if (isWaterlogged(state)){
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.5F, 1.0F);
        } else {
            level.playSound(null, pos, SoundEvents.GENERIC_EXTINGUISH_FIRE, SoundSource.BLOCKS, 0.3F, 1.0F);
        }

        dowse(player, level, pos, state);
        if (survivalMode){ heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand)); }

        return InteractionResult.SUCCESS;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        Block block = state.getBlock();
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean survivalMode = !(player.isCreative());
        boolean coalsPresent = hasAshWhenUnlit(state);

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
        return new CopperGlowCampfireBlockEntity(pos, blockState);
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
        Block newBlock = newState.getBlock();

        if (newBlock instanceof ICopperCampfireVariant){
            // Short-circuit if block oxidized or was waxed <-> unwaxed.
            return;
        }

        // Handle as usual if the block changed to anything else.
        if (!state.is(newBlock)) {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) {
                Containers.dropContents(level, pos, copperGlowCampfireBlockEntity.getItems());
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
        if (!level.isClientSide() && projectile.isOnFire() && projectile.mayInteract(level, blockpos) && hasAshWhenUnlit(blockState)) {
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
        if (blockEntity instanceof CopperGlowCampfireBlockEntity copperGlowCampfireBlockEntity) copperGlowCampfireBlockEntity.dowse();
        levelAccessor.gameEvent(entity, GameEvent.BLOCK_CHANGE, pos);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide) {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::particleTick) : null;
        } else {
            return blockState.getValue(LIT) ? createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::cookTick) : createTickerHelper(blockEntityType, ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireBlockEntity::cooldownTick);
        }
    }
}
