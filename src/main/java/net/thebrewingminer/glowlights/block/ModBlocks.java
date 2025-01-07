package net.thebrewingminer.glowlights.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.custom.GlowCampfireBlock;
import net.thebrewingminer.glowlights.block.custom.GlowLanternBlock;
import net.thebrewingminer.glowlights.block.custom.GlowTorchBlock;
import net.thebrewingminer.glowlights.block.custom.GlowWallTorchBlock;
import net.thebrewingminer.glowlights.items.ModItems;

import java.util.function.Supplier;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, GlowLights.MODID);

    public static final RegistryObject<Block> GLOW_LANTERN_BLOCK = registerBlock("glow_lantern_block",
            () -> new GlowLanternBlock(BlockBehaviour.Properties.copy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel)), CreativeModeTab.TAB_BUILDING_BLOCKS);

    public static final RegistryObject<Block> GLOW_TORCH_BLOCK = BLOCKS.register("glow_torch",
            () -> new GlowTorchBlock(BlockBehaviour.Properties.copy(Blocks.TORCH).lightLevel(GlowTorchBlock::getLightLevel), ParticleTypes.GLOW)
    );

    public static final RegistryObject<Block> GLOW_WALL_TORCH_BLOCK = BLOCKS.register("glow_wall_torch",
            () -> new GlowWallTorchBlock(BlockBehaviour.Properties.copy(Blocks.WALL_TORCH).lightLevel(GlowWallTorchBlock::getLightLevel), ParticleTypes.GLOW)
    );

    public static final RegistryObject<Block> PRISMARINE_GLOW_CAMPFIRE = BLOCKS.register("prismarine_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.copy(Blocks.PRISMARINE)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> PRISMARINE_BRICK_GLOW_CAMPFIRE = BLOCKS.register("prismarine_brick_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.copy(Blocks.PRISMARINE_BRICKS)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
            )
    );

    public static final RegistryObject<Block> DARK_PRISMARINE_GLOW_CAMPFIRE = BLOCKS.register("dark_prismarine_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.copy(Blocks.DARK_PRISMARINE)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
            )
    );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}