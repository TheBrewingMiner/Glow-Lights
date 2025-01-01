package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
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
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlowTorchBlock extends TorchBlock implements SimpleWaterloggedBlock {
    protected static final int AABB_STANDING_OFFSET = 2;
    protected static final VoxelShape AABB = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
    protected final ParticleOptions flameParticle;
    public static final BooleanProperty WATERLOGGED;

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    public GlowTorchBlock(BlockBehaviour.Properties properties, ParticleOptions particle) {
        super(properties, particle);
        this.flameParticle = particle;
        this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public BlockState updateShape(BlockState state1, Direction direction, BlockState state2, LevelAccessor accessor, BlockPos pos1, BlockPos pos2) {
        return direction == Direction.DOWN && !this.canSurvive(state1, accessor, pos1) ? Blocks.AIR.defaultBlockState() : super.updateShape(state1, direction, state2, accessor, pos1, pos2);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos) {
        return canSupportCenter(reader, pos.below(), Direction.UP);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return (Boolean) state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos blockPos, Player player, InteractionHand hand, BlockHitResult result){
        if(!level.isClientSide() && hand == InteractionHand.MAIN_HAND){
            level.setBlock(blockPos, state, 3);
        }
        return super.use(state, level, blockPos, player, hand, result);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(WATERLOGGED);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        double $$4 = (double)pos.getX() + 0.5;
        double $$5 = (double)pos.getY() + 0.7;
        double $$6 = (double)pos.getZ() + 0.5;
        level.addParticle(ParticleTypes.SMOKE, $$4, $$5, $$6, 0.0, 0.0, 0.0);
        level.addParticle(this.flameParticle, $$4, $$5, $$6, 0.0, 0.0, 0.0);
    }


}

//import net.minecraft.core.BlockPos;
//import net.minecraft.core.Direction;
//import net.minecraft.core.particles.ParticleOptions;
//import net.minecraft.core.particles.ParticleTypes;
//import net.minecraft.sounds.SoundEvent;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.item.Items;
//import net.minecraft.world.level.BlockGetter;
//import net.minecraft.world.level.Level;
//import net.minecraft.world.level.LevelAccessor;
//import net.minecraft.world.level.LevelReader;
//import net.minecraft.world.level.block.Block;
//import net.minecraft.world.level.block.Blocks;
//import net.minecraft.world.level.block.SimpleWaterloggedBlock;
//import net.minecraft.world.level.block.TorchBlock;
//import net.minecraft.world.level.block.state.BlockBehaviour;
//import net.minecraft.world.level.block.state.BlockState;
//import net.minecraft.world.level.block.state.properties.BlockStateProperties;
//import net.minecraft.world.level.block.state.properties.BooleanProperty;
//import net.minecraft.world.phys.shapes.CollisionContext;
//import net.minecraft.world.phys.shapes.VoxelShape;
//import net.minecraft.world.level.material.Fluid;
//import net.minecraft.world.level.material.FluidState;
//import net.minecraft.world.level.material.Fluids;
//import java.util.Optional;
//public class GlowTorchBlock extends TorchBlock implements SimpleWaterloggedBlock {
//    protected static final int AABB_STANDING_OFFSET = 2;
//    protected static final VoxelShape AABB = Block.box(6.0, 0.0, 6.0, 10.0, 10.0, 10.0);
////    protected final ParticleOptions flameParticle;
//    public static final BooleanProperty WATERLOGGED;
//
//    static {
//        WATERLOGGED = BlockStateProperties.WATERLOGGED;
//    }
//
//    public GlowTorchBlock(BlockBehaviour.Properties p_57491_, ParticleOptions p_57492_) {
//        super(p_57491_, p_57492_);
//    }
//
//    // Override the canPlaceLiquid method to allow waterlogging
//    @Override
//    public boolean canPlaceLiquid(BlockGetter p_56301_, BlockPos p_56302_, BlockState p_56303_, Fluid p_56304_) {
//        // Allow waterlogging if the block is not already waterlogged
//        return !(Boolean) p_56303_.getValue(BlockStateProperties.WATERLOGGED) && p_56304_ == Fluids.WATER;
//    }
//
//    // Override the placeLiquid method to handle waterlogging
//    @Override
//    public boolean placeLiquid(LevelAccessor p_56306_, BlockPos p_56307_, BlockState p_56308_, FluidState p_56309_) {
//        // Check if the block is not already waterlogged and is being waterlogged with water
//        if (!(Boolean) p_56308_.getValue(BlockStateProperties.WATERLOGGED) && p_56309_.getType() == Fluids.WATER) {
//            if (!p_56306_.isClientSide()) {
//                p_56306_.setBlock(p_56307_, p_56308_.setValue(BlockStateProperties.WATERLOGGED, true), 3);
//                p_56306_.scheduleTick(p_56307_, p_56309_.getType(), p_56309_.getType().getTickDelay(p_56306_));
//            }
//            return true;
//        }
//        return false;
//    }
//
//    // Override the pickupBlock method to handle the removal of water and return a water bucket
//    @Override
//    public ItemStack pickupBlock(LevelAccessor p_154560_, BlockPos p_154561_, BlockState p_154562_) {
//        // If the block is waterlogged, remove the waterlogging
//        if ((Boolean) p_154562_.getValue(BlockStateProperties.WATERLOGGED)) {
//            p_154560_.setBlock(p_154561_, p_154562_.setValue(BlockStateProperties.WATERLOGGED, false), 3);
//            if (!p_154562_.canSurvive(p_154560_, p_154561_)) {
//                p_154560_.destroyBlock(p_154561_, true);
//            }
//            return new ItemStack(Items.WATER_BUCKET);
//        }
//        return ItemStack.EMPTY;
//    }
//
//    // Override the getPickupSound method to return the correct sound for picking up water
//    @Override
//    public Optional<SoundEvent> getPickupSound() {
//        return Fluids.WATER.getPickupSound();
//    }
//
//    // Override the updateShape method to handle block behavior when surrounding blocks change
//    @Override
//    public BlockState updateShape(BlockState p_57503_, Direction p_57504_, BlockState p_57505_, LevelAccessor p_57506_, BlockPos p_57507_, BlockPos p_57508_) {
//        // Prevent the torch from becoming air if it's waterlogged
//        return p_57504_ == Direction.DOWN && !this.canSurvive(p_57503_, p_57506_, p_57507_) && !(Boolean) p_57503_.getValue(BlockStateProperties.WATERLOGGED)
//                ? Blocks.AIR.defaultBlockState()
//                : super.updateShape(p_57503_, p_57504_, p_57505_, p_57506_, p_57507_, p_57508_);
//    }
//
//    // Override the canSurvive method to check if the block can survive on the block below it
//    @Override
//    public boolean canSurvive(BlockState p_57499_, LevelReader p_57500_, BlockPos p_57501_) {
//        return canSupportCenter(p_57500_, p_57501_.below(), Direction.UP);
//    }
//}