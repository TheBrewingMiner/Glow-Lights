package net.thebrewingminer.glowlights.block;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
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
            () -> new GlowLanternBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SEA_LANTERN).lightLevel(GlowLanternBlock::getLightLevel)
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "glow_lantern_block")))
            )
    );

    public static final RegistryObject<Block> GLOW_TORCH_BLOCK = BLOCKS.register("glow_torch",
            () -> new GlowTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel(GlowTorchBlock::getLightLevel)
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "glow_torch"))), ParticleTypes.GLOW)
    );

    public static final RegistryObject<Block> GLOW_WALL_TORCH_BLOCK = BLOCKS.register("glow_wall_torch",
            () -> new GlowWallTorchBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TORCH).lightLevel(GlowWallTorchBlock::getLightLevel)
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "glow_wall_torch"))), ParticleTypes.GLOW)
    );

    public static final RegistryObject<Block> PRISMARINE_GLOW_CAMPFIRE = BLOCKS.register("prismarine_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "prismarine_glow_campfire")))
            )
    );

    public static final RegistryObject<Block> PRISMARINE_BRICK_GLOW_CAMPFIRE = BLOCKS.register("prismarine_brick_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.PRISMARINE_BRICKS)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "prismarine_brick_glow_campfire")))
            )
    );

    public static final RegistryObject<Block> DARK_PRISMARINE_GLOW_CAMPFIRE = BLOCKS.register("dark_prismarine_glow_campfire",
            () -> new GlowCampfireBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.DARK_PRISMARINE)
                    .lightLevel(GlowCampfireBlock::getLightLevel)
                    .noOcclusion()
                    .setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, "dark_prismarine_glow_campfire")))
            )
    );

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block){
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);

        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block){
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()
                .setId(ResourceKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(GlowLights.MODID, name))))
        );
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}