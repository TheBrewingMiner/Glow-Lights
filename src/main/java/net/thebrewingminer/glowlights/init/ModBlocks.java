package net.thebrewingminer.glowlights.init;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.*;
import net.thebrewingminer.glowlights.block.copper.*;

import java.util.function.Supplier;

public class ModBlocks {
    // Create the registry for custom blocks of this mod.
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlowLights.MOD_ID);

    // Registers a new item of this mod with the given block information.
    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    // Registers a new block AND its corresponding item.
    private static <T extends Block> RegistryObject<T> registerBlockAndItem(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);

        return toReturn;
    }

/* Blocks */

    public static final RegistryObject<Block> GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel))
    );

    public static final RegistryObject<Block> GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "glow_berry_lantern_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> GLOW_LANTERN = registerBlockAndItem(
        "glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.of(Material.METAL).strength(3.5F).sound(SoundType.LANTERN).lightLevel(GlowLantern::getLightLevel))
    );

    public static final RegistryObject<Block> GLOW_BERRY_LANTERN = registerBlockAndItem(
        "glow_berry_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(GLOW_LANTERN.get()).lightLevel((state) -> 15))
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
        () -> new GlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.PRISMARINE)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> PRISMARINE_BRICK_GLOW_CAMPFIRE = registerBlockAndItem(
        "prismarine_brick_glow_campfire",
        () -> new GlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.PRISMARINE_BRICKS)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> DARK_PRISMARINE_GLOW_CAMPFIRE = registerBlockAndItem(
        "dark_prismarine_glow_campfire",
        () -> new GlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.DARK_PRISMARINE)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> GLOW_CAMPFIRE = registerBlockAndItem(
        "glow_campfire",
        () -> new GlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.CAMPFIRE)
                .lightLevel(GlowCampfireBlock::getLightLevel),
            1,
            5
        )
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

    public static final RegistryObject<Block> COPPER_GLOW_LANTERN = registerBlockAndItem(
        "copper_glow_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(GLOW_LANTERN.get()).sound(SoundType.COPPER).lightLevel(CopperGlowLantern::getLightLevel)
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "exposed_copper_glow_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(CopperGlowLantern::getLightLevel)
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "weathered_copper_glow_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(CopperGlowLantern::getLightLevel)
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "oxidized_copper_glow_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(CopperGlowLantern::getLightLevel)
        )
    );

    public static final RegistryObject<Block> COPPER_CHAIN = registerBlockAndItem(
        "copper_chain",
        () -> new CopperChainBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.CHAIN)
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_CHAIN = registerBlockAndItem(
            "exposed_copper_chain",
        () -> new CopperChainBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.CHAIN)
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_CHAIN = registerBlockAndItem(
        "weathered_copper_chain",
        () -> new CopperChainBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.CHAIN)
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_CHAIN = registerBlockAndItem(
        "oxidized_copper_chain",
        () -> new CopperChainBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.CHAIN)
        )
    );

    public static final RegistryObject<Block> COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
    "copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.COPPER_BLOCK)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.75f,
            5
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "exposed_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.EXPOSED_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.65f,
            5
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "weathered_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.WEATHERED_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "oxidized_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.OXIDIZED_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "cut_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.CUT_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.75f,
            5
        )
    );

    public static final RegistryObject<Block> EXPOSED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "exposed_cut_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.EXPOSED_CUT_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.65f,
            5
        )
    );

    public static final RegistryObject<Block> WEATHERED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "weathered_cut_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.WEATHERED_CUT_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "oxidized_cut_copper_glow_campfire",
        () -> new CopperGlowCampfireBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.OXIDIZED_CUT_COPPER)
                .lightLevel(CopperGlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "copper_glow_lantern_block",
        () -> new CopperGlowLanternBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(CopperGlowLanternBlock::getLightLevel)
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "exposed_copper_glow_lantern_block",
        () -> new CopperGlowLanternBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(CopperGlowLanternBlock::getLightLevel)
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "weathered_copper_glow_lantern_block",
        () -> new CopperGlowLanternBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(CopperGlowLanternBlock::getLightLevel)
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "oxidized_copper_glow_lantern_block",
        () -> new CopperGlowLanternBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(CopperGlowLanternBlock::getLightLevel)
        )
    );

    public static final RegistryObject<Block> COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
    "copper_glow_berry_lantern_block",
        () -> new CopperFullBlock(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "exposed_copper_glow_berry_lantern_block",
        () -> new CopperFullBlock(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "weathered_copper_glow_berry_lantern_block",
        () -> new CopperFullBlock(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "oxidized_copper_glow_berry_lantern_block",
        () -> new CopperFullBlock(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "copper_glow_berry_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.UNAFFECTED,
            BlockBehaviour.Properties.copy(GLOW_BERRY_LANTERN.get()).sound(SoundType.COPPER).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> EXPOSED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "exposed_copper_glow_berry_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.EXPOSED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> WEATHERED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "weathered_copper_glow_berry_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.WEATHERED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15)
        )
    );

    public static final RegistryObject<Block> OXIDIZED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "oxidized_copper_glow_berry_lantern",
        () -> new CopperGlowLantern(
            WeatheringCopper.WeatherState.OXIDIZED,
            BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15)
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

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "waxed_copper_glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(GlowLantern::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "waxed_exposed_copper_glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(GlowLantern::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "waxed_weathered_copper_glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(GlowLantern::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_LANTERN = registerBlockAndItem(
        "waxed_oxidized_copper_glow_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_LANTERN.get()).lightLevel(GlowLantern::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_COPPER_CHAIN = registerBlockAndItem(
        "waxed_copper_chain",
        () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN))
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_CHAIN = registerBlockAndItem(
        "waxed_exposed_copper_chain",
        () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN))
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_CHAIN = registerBlockAndItem(
            "waxed_weathered_copper_chain",
            () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN))
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_CHAIN = registerBlockAndItem(
            "waxed_oxidized_copper_chain",
            () -> new ChainBlock(BlockBehaviour.Properties.copy(Blocks.CHAIN))
    );

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_COPPER_BLOCK)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.75f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_exposed_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_EXPOSED_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.65f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_weathered_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_WEATHERED_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_oxidized_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_OXIDIZED_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_cut_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_CUT_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.75f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_exposed_cut_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_EXPOSED_CUT_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.65f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_weathered_cut_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_WEATHERED_CUT_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE = registerBlockAndItem(
        "waxed_oxidized_cut_copper_glow_campfire",
        () -> new WaxedCopperGlowCampfireBlock(
            BlockBehaviour.Properties.copy(Blocks.WAXED_OXIDIZED_CUT_COPPER)
                .lightLevel(GlowCampfireBlock::getLightLevel)
                .noOcclusion(),
            0.5f,
            5
        )
    );

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_copper_glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_exposed_copper_glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_weathered_copper_glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_oxidized_copper_glow_lantern_block",
        () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel))
    );

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_copper_glow_berry_lantern_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_exposed_copper_glow_berry_lantern_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_weathered_copper_glow_berry_lantern_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK = registerBlockAndItem(
        "waxed_oxidized_copper_glow_berry_lantern_block",
        () -> new Block(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "waxed_copper_glow_berry_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_EXPOSED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "waxed_exposed_copper_glow_berry_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_WEATHERED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "waxed_weathered_copper_glow_berry_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15))
    );

    public static final RegistryObject<Block> WAXED_OXIDIZED_COPPER_GLOW_BERRY_LANTERN = registerBlockAndItem(
        "waxed_oxidized_copper_glow_berry_lantern",
        () -> new GlowLantern(BlockBehaviour.Properties.copy(COPPER_GLOW_BERRY_LANTERN.get()).lightLevel((state) -> 15))
    );

    // Make this registry known to the event bus.
    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}