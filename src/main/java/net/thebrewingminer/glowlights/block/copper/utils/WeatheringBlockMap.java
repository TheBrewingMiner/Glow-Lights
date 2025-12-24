package net.thebrewingminer.glowlights.block.copper.utils;

import com.google.common.base.Suppliers;
import com.google.common.collect.BiMap;
import com.google.common.collect.ImmutableBiMap;
import net.minecraft.world.level.block.Block;
import net.thebrewingminer.glowlights.init.ModBlocks;

import java.util.function.Supplier;

public class WeatheringBlockMap {
    public static Supplier<BiMap<Block, Block>> NEXT_BY_BLOCK = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                .put(ModBlocks.COPPER_GLOW_TORCH.get(), ModBlocks.EXPOSED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_TORCH.get(), ModBlocks.WEATHERED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_TORCH.get(), ModBlocks.OXIDIZED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.COPPER_GLOW_WALL_TORCH.get(), ModBlocks.EXPOSED_COPPER_GLOW_WALL_TORCH.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_WALL_TORCH.get(), ModBlocks.WEATHERED_COPPER_GLOW_WALL_TORCH.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_WALL_TORCH.get(), ModBlocks.OXIDIZED_COPPER_GLOW_WALL_TORCH.get())

                .put(ModBlocks.COPPER_GLOW_LANTERN.get(), ModBlocks.EXPOSED_COPPER_GLOW_LANTERN.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_LANTERN.get(), ModBlocks.WEATHERED_COPPER_GLOW_LANTERN.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_LANTERN.get(), ModBlocks.OXIDIZED_COPPER_GLOW_LANTERN.get())

                .put(ModBlocks.COPPER_CHAIN.get(), ModBlocks.EXPOSED_COPPER_CHAIN.get())
                .put(ModBlocks.EXPOSED_COPPER_CHAIN.get(), ModBlocks.WEATHERED_COPPER_CHAIN.get())
                .put(ModBlocks.WEATHERED_COPPER_CHAIN.get(), ModBlocks.OXIDIZED_COPPER_CHAIN.get())

                .put(ModBlocks.COPPER_GLOW_CAMPFIRE.get(), ModBlocks.EXPOSED_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WEATHERED_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.OXIDIZED_COPPER_GLOW_CAMPFIRE.get())

                .put(ModBlocks.CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE.get())
            .build();
    });

    public static Supplier<BiMap<Block, Block>> WAXABLES = Suppliers.memoize(() -> {
        return ImmutableBiMap.<Block, Block>builder()
                .put(ModBlocks.COPPER_GLOW_TORCH.get(), ModBlocks.WAXED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.COPPER_GLOW_WALL_TORCH.get(), ModBlocks.WAXED_COPPER_GLOW_WALL_TORCH.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_TORCH.get(), ModBlocks.WAXED_EXPOSED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_WALL_TORCH.get(), ModBlocks.WAXED_EXPOSED_COPPER_GLOW_WALL_TORCH.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_TORCH.get(), ModBlocks.WAXED_WEATHERED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_WALL_TORCH.get(), ModBlocks.WAXED_WEATHERED_COPPER_GLOW_WALL_TORCH.get())
                .put(ModBlocks.OXIDIZED_COPPER_GLOW_TORCH.get(), ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_TORCH.get())
                .put(ModBlocks.OXIDIZED_COPPER_GLOW_WALL_TORCH.get(), ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_WALL_TORCH.get())

                .put(ModBlocks.COPPER_GLOW_LANTERN.get(), ModBlocks.WAXED_COPPER_GLOW_LANTERN.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_LANTERN.get(), ModBlocks.WAXED_EXPOSED_COPPER_GLOW_LANTERN.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_LANTERN.get(), ModBlocks.WAXED_WEATHERED_COPPER_GLOW_LANTERN.get())
                .put(ModBlocks.OXIDIZED_COPPER_GLOW_LANTERN.get(), ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_LANTERN.get())

                .put(ModBlocks.COPPER_CHAIN.get(), ModBlocks.WAXED_COPPER_CHAIN.get())
                .put(ModBlocks.EXPOSED_COPPER_CHAIN.get(), ModBlocks.WAXED_EXPOSED_COPPER_CHAIN.get())
                .put(ModBlocks.WEATHERED_COPPER_CHAIN.get(), ModBlocks.WAXED_WEATHERED_COPPER_CHAIN.get())
                .put(ModBlocks.OXIDIZED_COPPER_CHAIN.get(), ModBlocks.WAXED_OXIDIZED_COPPER_CHAIN.get())

                .put(ModBlocks.COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.EXPOSED_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_EXPOSED_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.WEATHERED_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_WEATHERED_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.OXIDIZED_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_CAMPFIRE.get())

                .put(ModBlocks.CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_CUT_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get())
                .put(ModBlocks.OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE.get(), ModBlocks.WAXED_OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE.get())
            .build();
    });

    public static Supplier<BiMap<Block, Block>> PREVIOUS_BY_BLOCK = Suppliers.memoize(() -> NEXT_BY_BLOCK.get().inverse());
    public static Supplier<BiMap<Block, Block>> WAX_OFF_BY_BLOCK = Suppliers.memoize(() -> WAXABLES.get().inverse());
}