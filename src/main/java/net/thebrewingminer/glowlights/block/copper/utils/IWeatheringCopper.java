package net.thebrewingminer.glowlights.block.copper.utils;

import com.google.common.collect.BiMap;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Optional;

@SuppressWarnings({"NullableProblems", "rawtypes"})
public interface IWeatheringCopper extends WeatheringCopper {

    // Maps passed-in Block to its corresponding block in the weathering state map.
    // (Block) Key -> Value
    static Optional<Block> getNext(Block pBlock){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.NEXT_BY_BLOCK.get()).get(pBlock));
    }

    // Maps passed-in BlockState to its block's corresponding block in the weathering state map.
    // (BlockState) Key -> Value
    default Optional<BlockState> getNext(BlockState blockState){
        return getNext(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    // Maps passed-in Block to its corresponding previous block in the weathering state map.
    // (Block) Key <- Value
    static Optional<Block> getPrevious(Block pBlock){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(pBlock));
    }

    // Maps passed-in BlockState to its block's corresponding previous block in the weathering state map.
    // (BlockState) Key <- Value
    static Optional<BlockState> getPrevious(BlockState blockState){
        return getPrevious(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    // Gets the first block in the weathering state map for the passed-in block by iterating through the inverse of the weathering state map.
    // Get the previous block for the (Block) Key, then get the previous block of that result and so on, until there is no matching value for that key.
    static Block getFirst(Block pBlock){
        Block block = pBlock;
        for (Block mapBlock = (Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(pBlock); mapBlock != null; mapBlock = (Block)((BiMap)WeatheringBlockMap.PREVIOUS_BY_BLOCK.get()).get(mapBlock)) {
            block = mapBlock;
        }

        return block;
    }

    // Wrapper method of Block getFirst(Block) for passing in BlockStates.
    static BlockState getFirst(BlockState blockState){
        return getFirst(blockState.getBlock()).withPropertiesOf(blockState);
    }

    // Maps passed-in Block to its corresponding block in the waxable map.
    // (Block) Key -> Value
    static Optional<Block> getWaxed(Block block){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.WAXABLES.get()).get(block));
    }

    // Maps passed-in BlockState to its block's corresponding block in the waxable map.
    // (BlockState) Key -> Value
    static Optional<BlockState> getWaxed(BlockState blockState){
        return getWaxed(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }

    // Maps passed-in Block to its corresponding previous block in the waxable map.
    // (Block) Key <- Value
    static Optional<Block> getUnwaxed(Block block){
        return Optional.ofNullable((Block)((BiMap)WeatheringBlockMap.WAX_OFF_BY_BLOCK.get()).get(block));
    }

    // Maps passed-in BlockState to its block's corresponding previous block in the waxable map.
    // (BlockState) Key <- Value
    static Optional<BlockState> getUnwaxed(BlockState blockState){
        return getUnwaxed(blockState.getBlock()).map((block) -> block.withPropertiesOf(blockState));
    }
}