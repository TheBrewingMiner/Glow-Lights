package net.thebrewingminer.glowlights.block.custom;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlowWallTorchBlock extends WallTorchBlock implements SimpleWaterloggedBlock{
    public static final DirectionProperty FACING;
    protected static final float AABB_OFFSET = 2.5F;
    private static final Map<Direction, VoxelShape> AABBS;
    public static final BooleanProperty WATERLOGGED;

    public GlowWallTorchBlock(BlockBehaviour.Properties properties, SimpleParticleType particle) {
        super(particle, properties);
        this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(WATERLOGGED, false));
    }

    @Override
    public String getDescriptionId() {
        return this.asItem().getDescriptionId();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return getShape(state);
    }

    public static VoxelShape getShape(BlockState state) {
        return AABBS.get(state.getValue(FACING));
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        Direction $$3 = state.getValue(FACING);
        BlockPos $$4 = pos.relative($$3.getOpposite());
        BlockState $$5 = reader.getBlockState($$4);
        return $$5.isFaceSturdy(reader, $$4, $$3);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState $$1 = this.defaultBlockState();
        LevelReader $$2 = context.getLevel();
        BlockPos $$3 = context.getClickedPos();
        Direction[] $$4 = context.getNearestLookingDirections();
        Direction[] var6 = $$4;
        int var7 = $$4.length;

        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        boolean flag = levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER;

        for(int var8 = 0; var8 < var7; ++var8) {
            Direction $$5 = var6[var8];
            if ($$5.getAxis().isHorizontal()) {
                Direction $$6 = $$5.getOpposite();
                $$1 = $$1.setValue(FACING, $$6);
                if ($$1.canSurvive($$2, $$3)) {
                    return $$1.setValue(WATERLOGGED, flag);
                }
            }
        }

        return null;
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState state2, LevelAccessor level, BlockPos pos, BlockPos pos2) {
        return direction.getOpposite() == state.getValue(FACING) && !state.canSurvive(level, pos) ? Blocks.AIR.defaultBlockState() : state;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        Direction $$4 = state.getValue(FACING);
        double $$5 = (double)pos.getX() + 0.5;
        double $$6 = (double)pos.getY() + 0.7;
        double $$7 = (double)pos.getZ() + 0.5;
        double $$8 = 0.22;
        double $$9 = 0.27;
        Direction $$10 = $$4.getOpposite();

        if (state.getValue(WATERLOGGED)){
            if (source.nextInt(7) == 0){
                level.addParticle(ParticleTypes.SMOKE, $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
            }
            if(source.nextInt(3) == 0){
                level.addParticle(this.flameParticle, $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
            }
        } else {
            if (source.nextInt(2) == 0){
                level.addParticle(ParticleTypes.SMOKE, $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
            }

            if (source.nextInt(7) == 0){
                level.addParticle(this.flameParticle, $$5 + 0.27 * (double)$$10.getStepX(), $$6 + 0.22, $$7 + 0.27 * (double)$$10.getStepZ(), 0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        int lightLevel = (isWaterlogged(state)) ? 15 : 10;
        return lightLevel;
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result){
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND){
            level.setBlock(blockPos, state, 3);
        }
        return super.use(state, level, blockPos, player, hand, result);
    }

    static {
        FACING = HorizontalDirectionalBlock.FACING;
        AABBS = Maps.newEnumMap(ImmutableMap.of(Direction.NORTH, Block.box(5.5, 3.0, 11.0, 10.5, 13.0, 16.0), Direction.SOUTH, Block.box(5.5, 3.0, 0.0, 10.5, 13.0, 5.0), Direction.WEST, Block.box(11.0, 3.0, 5.5, 16.0, 13.0, 10.5), Direction.EAST, Block.box(0.0, 3.0, 5.5, 5.0, 13.0, 10.5)));
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }
}