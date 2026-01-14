package net.thebrewingminer.glowlights.block.utils;

import net.minecraft.world.level.block.state.properties.BooleanProperty;

// Mimics Vanilla's BlockStateProperties to remain aligned with code's style.
public class BlockStateProperty {

    // Custom block state property for Glow Campfires.
    public static final BooleanProperty HAS_ASH_UNLIT = BooleanProperty.create("has_ash_unlit");
}