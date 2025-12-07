package net.thebrewingminer.glowlights.block.copper.utils;

import com.google.common.collect.BiMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

public interface IWeatheringCopper extends WeatheringCopper {

    static Optional<Block> getNext(Block pBlock){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.NEXT_BY_BLOCK.get()).get(pBlock));
    }

    default Optional<BlockState> getNext(BlockState blockState){
        return getNext(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    static Optional<Block> getPrevious(Block pBlock){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(pBlock));
    }

    static Optional<BlockState> getPrevious(BlockState blockState){
        return getPrevious(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    static Block getFirst(Block pBlock){
        Block block = pBlock;
        for(Block mapBlock = (Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(pBlock); mapBlock != null; mapBlock = (Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(mapBlock)) {
            block = mapBlock;
        }

        return block;
    }

    static BlockState getFirst(BlockState blockState){
        return getFirst(blockState.getBlock()).withPropertiesOf(blockState);
    }

    static Optional<Block> getWaxed(Block block){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.WAXABLES.get()).get(block));
    }

    static Optional<BlockState> getWaxed(BlockState blockState){
        return getWaxed(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    static Optional<Block> getUnwaxed(Block block){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.WAX_OFF_BY_BLOCK.get()).get(block));
    }

    static Optional<BlockState> getUnwaxed(BlockState blockState){
        return getUnwaxed(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }
}