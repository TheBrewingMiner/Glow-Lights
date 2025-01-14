package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class GlowCampfireBlock extends Block implements SimpleWaterloggedBlock {
    protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
    public static final BooleanProperty LIT;
    public static final BooleanProperty WATERLOGGED;
    public static final EnumProperty<Direction> FACING;
    public static final VoxelShape VIRTUAL_FENCE_POST;

    static {
        LIT = BlockStateProperties.LIT;
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
        FACING = BlockStateProperties.HORIZONTAL_FACING;
        VIRTUAL_FENCE_POST = Block.box(6.0, 0.0, 6.0, 10.0, 16.0, 10.0);
    }

    public GlowCampfireBlock(BlockBehaviour.Properties properties) {
        super(properties);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(LIT, false)
                .setValue(WATERLOGGED, false)
                .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(LIT, WATERLOGGED, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context){
        LevelAccessor levelaccessor = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        boolean flag = levelaccessor.getFluidState(blockpos).getType() == Fluids.WATER;
        return this.defaultBlockState().setValue(WATERLOGGED, flag).setValue(LIT, false).setValue(FACING, context.getHorizontalDirection());
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context) {
        return SHAPE;
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
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
        if (isLit(state)){
            lightLevel = (isWaterlogged(state)) ? 15 : 10;
        }

        return lightLevel;
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected InteractionResult useItemOn(ItemStack itemStack, BlockState blockState, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result) {
        ItemStack heldItem = player.getItemInHand(hand);
        boolean playerInSurvival = !player.isCreative();

        if (!blockState.getValue(LIT)){
            if (heldItem.is(Items.FLINT_AND_STEEL) || heldItem.is(Items.FIRE_CHARGE)){
                level.setBlock(pos, blockState.setValue(LIT, true), 3);
                if (playerInSurvival){
                    if (heldItem.is(Items.FLINT_AND_STEEL)){
                        heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                    } else if (heldItem.is(Items.FIRE_CHARGE)){
                        heldItem.shrink(1);
                    }
                }
                player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                return InteractionResult.SUCCESS;
            }
        } else if (blockState.getValue(LIT)){
            if(heldItem.getItem() instanceof ShovelItem){
                level.setBlock(pos, blockState.setValue(LIT, false), 3);
                if (playerInSurvival){
                    heldItem.hurtAndBreak(1, player, EquipmentSlot.MAINHAND);
                }
                player.awardStat(Stats.INTERACT_WITH_CAMPFIRE);
                return InteractionResult.SUCCESS;
            }
        }

        return super.useItemOn(itemStack, blockState, level, pos, player, hand, result);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource source) {
        if (state.getValue(LIT)) {
            if (state.getValue(WATERLOGGED)){
                if (source.nextInt(6) == 0) {
                    level.addParticle(ParticleTypes.GLOW, (double)pos.getX() + 0.5, (double)pos.getY() + 0.5, (double)pos.getZ() + 0.5, (double)(source.nextFloat() / 2.0F), 5.0E-5, (double)(source.nextFloat() / 2.0F));
                }
            }  else if (source.nextInt(10) == 0) {
                level.addParticle(ParticleTypes.GLOW, (double) pos.getX() + 0.5, (double) pos.getY() + 0.5, (double) pos.getZ() + 0.5, (double) (source.nextFloat() / 2.0F), 5.0E-5, (double) (source.nextFloat() / 2.0F));
            }
        }
    }
}