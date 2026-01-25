package net.thebrewingminer.glowlights.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.Tags;
import net.thebrewingminer.glowlights.block.utils.BlockStateProperty;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.init.ModParticles;

import javax.annotation.Nullable;

import static net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils.*;
import static net.thebrewingminer.glowlights.block.utils.GlowUtils.isWaterlogged;

@SuppressWarnings({"NullableProblems", "deprecation"})
public abstract class AbstractGlowCampfireBlock extends BaseEntityBlock implements SimpleWaterloggedBlock {

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
        HAS_ASH_UNLIT = BlockStateProperty.HAS_ASH_UNLIT;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    // Creates an object that mimics CampfireBlock with shared logic for both GlowCampfireBlock and CopperGlowCampfireBlock.
    protected AbstractGlowCampfireBlock(BlockBehaviour.Properties properties, float fireDamage, float fireDamageDelay) {
        super(properties);
        this.fireDamage = fireDamage;
        this.fireDamageDelay = fireDamageDelay;
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH).setValue(SIGNAL_FIRE, false).setValue(HAS_ASH_UNLIT, false));
    }

    // Return this object's VoxelShape
    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    // Return this object's RenderShape
    @Override
    public RenderShape getRenderShape(BlockState pState) {
        return RenderShape.MODEL;
    }

    // Add LIT, WATERLOGGED, FACING, SIGNAL_FIRE, and HAS_ASH_UNLIT blockstate properties to the state definition of this object.
    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(LIT, WATERLOGGED, FACING, SIGNAL_FIRE, HAS_ASH_UNLIT);
    }

    // Check if the block is placed in water for direct waterlogging and if a hay bale is underneath for SIGNAL_FIRE upon placement,
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext){
        LevelAccessor levelAccessor = blockPlaceContext.getLevel();
        BlockPos pos = blockPlaceContext.getClickedPos();
        boolean inWater = levelAccessor.getFluidState(pos).getType() == Fluids.WATER;
        return ( this.defaultBlockState().setValue(WATERLOGGED, inWater).setValue(LIT, false).setValue(FACING, blockPlaceContext.getHorizontalDirection()).setValue(SIGNAL_FIRE, this.isSmokeSource(levelAccessor.getBlockState(pos.below()))) );
    }

    // Ensure fluid is ticked properly if waterlogged, and that changes in the block below affect SIGNAL_FIRE as expected.
    @Override
    public BlockState updateShape(BlockState blockState, Direction facing, BlockState facingState, LevelAccessor level, BlockPos currentPos, BlockPos facingPos) {
        if (blockState.getValue(WATERLOGGED)) {
            level.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(level));
        }

        return (facing == Direction.DOWN ? blockState.setValue(SIGNAL_FIRE, this.isSmokeSource(facingState)) : super.updateShape(blockState, facing, facingState, level, currentPos, facingPos));
    }

    // Return false for being not pathfindable for mobs.
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

    // The light level behavior expected of this block.
    // Method reference is supplied to the block property call for light level in ModBlocks.
    public static int getLightLevel(BlockState state){
        int lightLevel = 0;
        if (isLit(state)){ lightLevel = (isWaterlogged(state)) ? 15 : 10; }

        return lightLevel;
    }

    // Child classes are expected to implement handling of campfire recipes with their respective block-entities.
    public abstract InteractionResult handleCampfireRecipe(Level level, Player player, ItemStack heldItem, BlockEntity blockEntity);

    // Handles interactions with ShovelItem objects when the campfire is unlit with ash.
    // Updates the block's state, plays a sound, and returns a piece of charcoal in survival mode.
    public static InteractionResult handleCleaningCampfire(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode){
        level.setBlock(pos, GlowCampfireUtils.cleanAsh(state), 3);
        level.playSound(null, pos, SoundEvents.SHOVEL_FLATTEN, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (survivalMode) {
            heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand));

            ItemStack recoveredCharcoal = new ItemStack(Items.CHARCOAL, 1);
            Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), recoveredCharcoal);
        }
        return InteractionResult.SUCCESS;
    }

    // Handles interactions with items in the coals tag.
    // Sets HAS_ASH_UNLIT to true and plays a sound.
    public static InteractionResult handleRefuelingCampfire(Level level, BlockState state, BlockPos pos, ItemStack heldItem, boolean survivalMode){
        level.setBlock(pos, GlowCampfireUtils.addCoals(state), 3);
        level.playSound(null, pos, SoundEvents.BASALT_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (survivalMode) heldItem.shrink(1);
        return InteractionResult.SUCCESS;
    }

    // Child objects are expected to implement dowsing handling with their respective block-entities.
    public abstract InteractionResult handleDowsing(Level level, Player player, InteractionHand playerHand, ItemStack heldItem, BlockState state, BlockPos pos, boolean survivalMode);

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand playerHand, BlockHitResult hitResult){
        ItemStack heldItem = player.getItemInHand(playerHand);
        BlockEntity blockEntity = level.getBlockEntity(pos);
        boolean survivalMode = !(player.isCreative());
        boolean coalsPresent = hasAshWhileUnlit(state);

        if (isUnlit(state)){ // If the campfire is NOT lit.
            if (level.isClientSide()) return InteractionResult.PASS;    // On the server only

            if (coalsPresent){
                // If the coals are still in the campfire (has ash when unlit)
                // Handles "cleaning" the coals from the campfire.
                if (heldItem.is(Tags.Items.TOOLS_SHOVELS)) return handleCleaningCampfire(level, player, playerHand, heldItem, state, pos, survivalMode);
            } else {
                // If coals are not still in the campfire
                if (heldItem.is(ItemTags.COALS)) return handleRefuelingCampfire(level, state, pos, heldItem, survivalMode); // Handles refueling.
            }
        } else {
            // Handle dowsing a LIT campfire.
            if (heldItem.is(Tags.Items.TOOLS_SHOVELS)) return handleDowsing(level, player, playerHand, heldItem, state, pos, survivalMode);
        }

        // Handle actions for campfire recipes (Vanilla).
        return handleCampfireRecipe(level, player, heldItem, blockEntity);
    }

    // Ambient glow squid particle with a specified 1/[delay] chance of occurring.
    public static void addAmbientGlowParticle(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0) {
            level.addParticle(ParticleTypes.GLOW, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, randomSource.nextFloat() / 2.0F, 5.0E-5, randomSource.nextFloat() / 2.0F);
        }
    }

    // Ambient campfire sound with a specified 1/[delay] chance of occurring.
    public static void playAmbientSound(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0){
            level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource){
        if (isUnlit(blockState)) return;    // Nothing happens if there is no flame.

        // Ambient events using specified delays to reduce the amount of sounds and particles.
        if (isWaterlogged(blockState)){
            addAmbientGlowParticle(level, pos, randomSource, WATERLOGGED_PARTICLE_DELAY);
            playAmbientSound(level, pos, randomSource, WATERLOGGED_SOUND_DELAY);
        } else {
            addAmbientGlowParticle(level, pos, randomSource, DRY_PARTICLE_DELAY);
            playAmbientSound(level, pos, randomSource, DRY_SOUND_DELAY);
        }
    }

    @Override
    public void entityInside(BlockState state, Level level, BlockPos pos, Entity entity) {
        // Damage eligible entities; more so if in water.
        if (isLit(state) && entity instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity)entity)) {
            if (isWaterlogged(state)){
                entity.hurt(DamageSource.IN_FIRE, this.fireDamage * 2.5f);
            } else {
                entity.hurt(DamageSource.IN_FIRE, this.fireDamage);
            }
        }

        super.entityInside(state, level, pos, entity);
    }

    // Child classes are expected to add their block entity to the world.
    @Override
    public abstract BlockEntity newBlockEntity(BlockPos pos, BlockState blockState);

    // Called by the game to handle removing the block.
    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving){
        if (!state.is(newState.getBlock())) {   // If the block changed
            onBlockEntityRemoved(level, pos);   // Call to handle block entity removal.
            super.onRemove(state, level, pos, newState, isMoving);  // Superclass's method handles removal of the block entity.
        }
    }

    // Child classes are expected to implement handling of the block-entity upon removal.
    protected abstract void onBlockEntityRemoved(Level level, BlockPos pos);

    // Called to determine if a hay block is underneath (passed-in blockstate is below the block).
    protected boolean isSmokeSource(BlockState pState) {
        return pState.is(Blocks.HAY_BLOCK);
    }

    // Handles projectile interactions (e.g. flaming arrows, dispensed fire charge).
    @Override
    public void onProjectileHit(Level level, BlockState blockState, BlockHitResult blockHitResult, Projectile projectile) {
        BlockPos blockpos = blockHitResult.getBlockPos();
        if (!level.isClientSide() && projectile.isOnFire() && projectile.mayInteract(level, blockpos) && hasAshWhileUnlit(blockState)) {
            level.setBlock(blockpos, litFromAsh(blockState), 11);
        }
    }

    // Used to handle particle spawning. Intended to be called by particleTick method in a blockEntity and by dowsing method(s).
    public static void makeParticles(Level level, BlockPos pos, boolean isSignalFire, boolean spawnExtraSmoke) {
        RandomSource randomsource = level.getRandom();
        SimpleParticleType simpleParticleType = isSignalFire ? ModParticles.SIGNAL_GLOW_SMOKE.get() : ModParticles.COZY_GLOW_SMOKE.get();
        level.addAlwaysVisibleParticle(simpleParticleType, true, (double)pos.getX() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1), (double)pos.getY() + randomsource.nextDouble() + randomsource.nextDouble(), (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 3.0 * (double)(randomsource.nextBoolean() ? 1 : -1), 0.0, 0.07, 0.0);
        if (spawnExtraSmoke) {
            level.addParticle(ParticleTypes.GLOW_SQUID_INK, (double)pos.getX() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1), (double)pos.getY() + 0.4, (double)pos.getZ() + 0.5 + randomsource.nextDouble() / 4.0 * (double)(randomsource.nextBoolean() ? 1 : -1), 0.0, 0.005, 0.0);
        }
    }

    // Child classes are expected to implement dowsing of the campfire.
    public abstract void dowse(@Nullable Entity entity, LevelAccessor levelAccessor, BlockPos pos, BlockState state);

    // Child classes are expected to implement ticking methods for the block entity.
    @Override
    @Nullable
    public abstract <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> blockEntityType);
}