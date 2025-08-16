package net.thebrewingminer.glowlights.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.custom.*;
import net.thebrewingminer.glowlights.block.custom.copper.CopperGlowTorchBlock;
import net.thebrewingminer.glowlights.block.custom.copper.CopperGlowWallTorchBlock;
import net.thebrewingminer.glowlights.items.ModItems;

import java.util.function.Supplier;


public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlowLights.MOD_ID);

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    private static <T extends Block> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> block, CreativeModeTab tab){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);

        return toReturn;
    }

/* Blocks */

    public static final RegistryObject<Block> GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel)),
        CreativeModeTab.TAB_BUILDING_BLOCKS
    );

    public static final RegistryObject<Block> GLOW_LANTERN = registerBlockAndItem(
        "glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(Blocks.LANTERN).lightLevel(GlowLantern::getLightLevel)),
        CreativeModeTab.TAB_DECORATIONS
    );

    public static final RegistryObject<Block> GLOW_TORCH = BLOCKS.register(
        "glow_torch",
        () -> new GlowTorchBlock(
                BlockBehaviour.Properties.copy(Blocks.TORCH).lightLevel(GlowTorchBlock::getLightLevel),
                ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> GLOW_WALL_TORCH = BLOCKS.register(
        "glow_wall_torch",
        () -> new GlowWallTorchBlock(
                BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lightLevel(GlowWallTorchBlock::getLightLevel),
                ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> PRISMARINE_GLOW_CAMPFIRE = registerBlockAndItem(
        "prismarine_glow_campfire",
        () -> new GlowCampfireBlockDeprecated(
                BlockBehaviour.Properties.copy(Blocks.PRISMARINE)
                .lightLevel(GlowCampfireBlockDeprecated::getLightLevel)
                .noOcclusion()
        ),
        CreativeModeTab.TAB_DECORATIONS
    );

    public static final RegistryObject<Block> PRISMARINE_BRICK_GLOW_CAMPFIRE = registerBlockAndItem(
        "prismarine_brick_glow_campfire",
        () -> new GlowCampfireBlockDeprecated(
                BlockBehaviour.Properties.copy(Blocks.PRISMARINE_BRICKS)
                .lightLevel(GlowCampfireBlockDeprecated::getLightLevel)
                .noOcclusion()
        ),
        CreativeModeTab.TAB_DECORATIONS
    );

    public static final RegistryObject<Block> DARK_PRISMARINE_GLOW_CAMPFIRE = registerBlockAndItem(
        "dark_prismarine_glow_campfire",
        () -> new GlowCampfireBlockDeprecated(
                BlockBehaviour.Properties.copy(Blocks.DARK_PRISMARINE)
                .lightLevel(GlowCampfireBlockDeprecated::getLightLevel)
                .noOcclusion()
        ),
        CreativeModeTab.TAB_DECORATIONS
    );

        /* Copper  Variants */

    public static final RegistryObject<Block> COPPER_GLOW_TORCH = BLOCKS.register(
        "copper_glow_torch",
        () -> new CopperGlowTorchBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "copper_glow_wall_torch",
        () -> new CopperGlowWallTorchBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).sound(SoundType.COPPER).lightLevel(CopperGlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_TORCH = BLOCKS.register(
        "exposed_copper_glow_torch",
        () -> new CopperGlowTorchBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "exposed_copper_glow_wall_torch",
        () -> new CopperGlowWallTorchBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_TORCH = BLOCKS.register(
        "weathered_copper_glow_torch",
        () -> new CopperGlowTorchBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "weathered_copper_glow_wall_torch",
        () -> new CopperGlowWallTorchBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_TORCH = BLOCKS.register(
        "oxidized_copper_glow_torch",
        () -> new CopperGlowTorchBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "oxidized_copper_glow_wall_torch",
        () -> new CopperGlowWallTorchBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).lightLevel(CopperGlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

        /* Waxed Copper Blocks */

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_TORCH = BLOCKS.register(
        "waxed_copper_glow_torch",
        () -> new GlowTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(GlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "waxed_copper_glow_wall_torch",
        () -> new GlowWallTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).sound(SoundType.COPPER).lightLevel(GlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_TORCH = BLOCKS.register(
        "waxed_exposed_copper_glow_torch",
        () -> new GlowTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(GlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "waxed_exposed_copper_glow_wall_torch",
        () -> new GlowWallTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).sound(SoundType.COPPER).lightLevel(GlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_TORCH = BLOCKS.register(
        "waxed_weathered_copper_glow_torch",
        () -> new GlowTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(GlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "waxed_weathered_copper_glow_wall_torch",
        () -> new GlowWallTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).sound(SoundType.COPPER).lightLevel(GlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_TORCH = BLOCKS.register(
    "waxed_oxidized_copper_glow_torch",
        () -> new GlowTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.TORCH).sound(SoundType.COPPER).lightLevel(GlowTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_WALL_TORCH = BLOCKS.register(
        "waxed_oxidized_copper_glow_wall_torch",
        () -> new GlowWallTorchBlock(
            BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).sound(SoundType.COPPER).sound(SoundType.COPPER).lightLevel(GlowWallTorchBlock::getLightLevel),
            ParticleTypes.GLOW
        )
    );


    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}