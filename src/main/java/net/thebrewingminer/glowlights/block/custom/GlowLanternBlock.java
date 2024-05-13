package net.thebrewingminer.glowlights.block.custom;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;


public class GlowLanternBlock extends Block implements SimpleWaterloggedBlock {
    public static final BooleanProperty LIT = BooleanProperty.create("lit");
    public FluidState getFluidState(BlockState p_56131_) {
        return (Boolean)p_56131_.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(p_56131_);
    }
    public GlowLanternBlock(Properties properties){
        super(properties);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder){
        builder.add(LIT);
    }
}
