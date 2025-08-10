package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;

public class GlowWallTorchBlock extends WallTorchBlock implements SimpleWaterloggedBlock{
    public static final DirectionProperty FACING;
    public static final BooleanProperty WATERLOGGED;

    public static final int WATERLOGGED_SMOKE_DELAY = 7;
    public static final int WATERLOGGED_PARTICLE_DELAY = 3;
    public static final int DRY_SMOKE_DELAY = 2;
    public static final int DRY_PARTICLE_DELAY = 7;

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    public GlowWallTorchBlock(BlockBehaviour.Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        Direction facindDirection = state.getValue(FACING);
        BlockPos oppositePos = pos.relative(facindDirection.getOpposite());
        BlockState blockState = reader.getBlockState(oppositePos);
        return blockState.isFaceSturdy(reader, oppositePos, facindDirection);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState blockState = this.defaultBlockState();
        LevelReader levelContext = context.getLevel();
        BlockPos pos = context.getClickedPos();
        Direction[] lookingDirections = context.getNearestLookingDirections();
        Direction[] lookingDirections2 = lookingDirections;
        int directionsLookingAt = lookingDirections.length;

        LevelAccessor levelaccessor = context.getLevel();
        BlockPos clickedPos = context.getClickedPos();
        boolean inWater = levelaccessor.getFluidState(clickedPos).getType() == Fluids.WATER;

        for(int i = 0; i < directionsLookingAt; ++i) {
            Direction direction = lookingDirections2[i];
            if (direction.getAxis().isHorizontal()) {
                Direction oppositeDirection = direction.getOpposite();
                blockState = blockState.setValue(FACING, oppositeDirection);
                if (blockState.canSurvive(levelContext, pos)) {
                    return blockState.setValue(WATERLOGGED, inWater);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));

        }
        return pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        return ((isWaterlogged(state)) ? 15 : 10);
    }

    public static void addFlameParticle(ParticleOptions particle, Level level, BlockState blockState, BlockPos pos, RandomSource randomSource, int delay){
        Direction facingDirection = blockState.getValue(FACING);
        Direction directionOpposite = facingDirection.getOpposite();
        double centerX = (double)pos.getX() + 0.5;
        double centerY = (double)pos.getY() + 0.7;
        double centerZ = (double)pos.getZ() + 0.5;

        double xzOffset = 0.27;
        double yOffset = 0.22;

        double xSpeed = 0.0;
        double ySpeed = 0.0;
        double zSpeed = 0.0;

        double x = centerX + xzOffset * (double)directionOpposite.getStepX();
        double y = centerY + yOffset;
        double z = centerZ + xzOffset * (double)directionOpposite.getStepZ();

        if (randomSource.nextInt(delay) == 0) {
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }

    @Override
    public void animateTick(BlockState blockState, Level level, BlockPos pos, RandomSource randomSource) {
        if (blockState.getValue(WATERLOGGED)){
            addFlameParticle(ParticleTypes.SMOKE, level, blockState, pos, randomSource, WATERLOGGED_SMOKE_DELAY);
            addFlameParticle(this.flameParticle, level, blockState, pos, randomSource, WATERLOGGED_PARTICLE_DELAY);
        } else {
            addFlameParticle(ParticleTypes.SMOKE, level, blockState, pos, randomSource, DRY_SMOKE_DELAY);
            addFlameParticle(this.flameParticle, level, blockState, pos, randomSource, DRY_PARTICLE_DELAY);
        }
    }
}