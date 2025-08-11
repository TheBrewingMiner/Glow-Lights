package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
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

@SuppressWarnings({"NullableProblems", "deprecation"})
public class GlowCampfireBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;
    public static final DirectionProperty FACING;
    public static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final VoxelShape VIRTUAL_FENCE_POST;

    public static final int WATERLOGGED_PARTICLE_DELAY = 6;
    public static final int DRY_PARTICLE_DELAY = 10;

    public static final int WATERLOGGED_SOUND_DELAY = 75;
    public static final int DRY_SOUND_DELAY = 32;

    static {
        LIT = BlockStateProperties.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    public GlowCampfireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(LIT, false).setValue(WATERLOGGED, false).setValue(FACING, Direction.NORTH));
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
        builder.add(LIT, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext){
        LevelAccessor level = blockPlaceContext.getLevel();
        BlockPos pos = blockPlaceContext.getClickedPos();
        boolean inWater = level.getFluidState(pos).getType() == Fluids.WATER;
        return ( this.defaultBlockState().setValue(WATERLOGGED, inWater).setValue(LIT, false).setValue(FACING, blockPlaceContext.getHorizontalDirection()) );
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
        boolean survivalMode = !(player.isCreative());

        if (!(state.getValue(LIT))){
            if (heldItem.is(Items.FLINT_AND_STEEL) || heldItem.is(Items.FIRE_CHARGE)){
                level.setBlock(pos, state.setValue(LIT, true), 3);
                if (survivalMode){
                    if (heldItem.is(Items.FLINT_AND_STEEL)){
                        heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand));
                    } else {
                        heldItem.shrink(1);
                    }
                }

                player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        } else {
            if (heldItem.is(Tags.Items.TOOLS_SHOVELS)){
                level.setBlock(pos, state.setValue(LIT, false), 3);
                if (survivalMode){ heldItem.hurtAndBreak(1, player, (p) -> p.broadcastBreakEvent(playerHand)); }

                player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                return InteractionResult.sidedSuccess(level.isClientSide);
            }
        }
        return super.use(state, level, pos, player, playerHand, hitResult);
    }

    public static void addGlowParticle(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0) {
            level.addParticle(ParticleTypes.GLOW, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, randomSource.nextFloat() / 2.0F, 5.0E-5, randomSource.nextFloat() / 2.0F);
        }
    }

    public static void playSound(Level level, BlockPos pos, RandomSource randomSource, int delay){
        if (randomSource.nextInt(delay) == 0){
            level.playLocalSound((double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, SoundEvents.CAMPFIRE_CRACKLE, SoundSource.BLOCKS, 0.5F + randomSource.nextFloat(), randomSource.nextFloat() * 0.7F + 0.6F, false);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource){
        if (!(blockState.getValue(LIT))) return;

        if (blockState.getValue(WATERLOGGED)){
            addGlowParticle(level, pos, randomSource, WATERLOGGED_PARTICLE_DELAY);
            playSound(level, pos, randomSource, WATERLOGGED_SOUND_DELAY);
        } else {
            addGlowParticle(level, pos, randomSource, DRY_PARTICLE_DELAY);
            playSound(level, pos, randomSource, DRY_SOUND_DELAY);
        }
    }
}