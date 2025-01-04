package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;


public class GlowLanternBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty WATERLOGGED;

    static {
        WATERLOGGED = BlockStateProperties.WATERLOGGED;
    }

    public GlowLanternBlock(Properties properties){
        super(properties);
        this.registerDefaultState((BlockState)this.stateDefinition.any().setValue(WATERLOGGED, false));
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

    public static boolean isWaterlogged(BlockState state){
        return state.getValue(WATERLOGGED);
    }

    public static int getLightLevel(BlockState state){
        int lightLevel = (isWaterlogged(state)) ? 15 : 10;
        return lightLevel;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(WATERLOGGED);
    }
}