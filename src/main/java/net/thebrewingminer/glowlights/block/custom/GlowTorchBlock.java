package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.TorchBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

@SuppressWarnings({"NullableProblems", "deprecation"})
public class GlowTorchBlock extends TorchBlock implements SimpleWaterloggedBlock {
    protected static final VoxelShape AABB = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    protected final ParticleOptions flameParticle;
    public static final BooleanProperty WATERLOGGED;

    public static final int WATERLOGGED_SMOKE_DELAY = 7;
    public static final int WATERLOGGED_PARTICLE_DELAY = 3;
    public static final int DRY_SMOKE_DELAY = 2;
    public static final int DRY_PARTICLE_DELAY = 7;

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    public GlowTorchBlock(BlockBehaviour.Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.flameParticle = particle;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(WATERLOGGED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockState updateShape(BlockState pState, Direction pFacing, BlockState pFacingState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.getValue(WATERLOGGED)) {
            pLevel.scheduleTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickDelay(pLevel));
        }

        return pFacing == Direction.DOWN && !this.canSurvive(pState, pLevel, pCurrentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        LevelAccessor levelAccessor = context.getLevel();
        BlockPos pos = context.getClickedPos();
        boolean inWater = levelAccessor.getFluidState(pos).getType() == Fluids.WATER;
        return (this.defaultBlockState().setValue(WATERLOGGED, inWater));
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }
    
    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        return (isWaterlogged(state)) ? 15 : 10;
    }

    public static void addFlameParticle(ParticleOptions particle, Level level, BlockPos pos, RandomSource randomSource, int delay){
        double x = (double)pos.getX() + 0.5;
        double y = (double)pos.getY() + 0.7;
        double z = (double)pos.getZ() + 0.5;
        double xSpeed = 0.0;
        double ySpeed = 0.0;
        double zSpeed = 0.0;

        if (randomSource.nextInt(delay) == 0) {
            level.addParticle(particle, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
    
    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource randomSource) {
        if (state.getValue(WATERLOGGED)) {
            addFlameParticle(ParticleTypes.SMOKE, level, pos, randomSource, WATERLOGGED_SMOKE_DELAY);
            addFlameParticle(this.flameParticle, level, pos, randomSource, WATERLOGGED_PARTICLE_DELAY);
        } else {
            addFlameParticle(ParticleTypes.SMOKE, level, pos, randomSource, DRY_SMOKE_DELAY);
            addFlameParticle(this.flameParticle, level, pos, randomSource,DRY_PARTICLE_DELAY);
        }
    }
}