package net.thebrewingminer.glowlights.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.block.entity.GlowCampfireBlockEntity;

public class ModBlockEntities {
    // Creates a registry for custom block entities for this mod.
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GlowLights.MOD_ID);

    // Registers GlowCampfireBlockEntity and ties it to its proper blocks.
    public static final RegistryObject<BlockEntityType<GlowCampfireBlockEntity>> GLOW_CAMPFIRE = BLOCK_ENTITIES.register(
        "glow_campfire",
        () -> BlockEntityType.Builder.of(GlowCampfireBlockEntity::new,
                                        ModBlocks.PRISMARINE_GLOW_CAMPFIRE.get(),
                                        ModBlocks.PRISMARINE_BRICK_GLOW_CAMPFIRE.get(),
                                        ModBlocks.DARK_PRISMARINE_GLOW_CAMPFIRE.get(),
                                        ModBlocks.GLOW_CAMPFIRE.get()
        ).build(null)
    );

    // Registers CopperGlowCampfireBlockEntity and ties it to its proper blocks.
    public static final RegistryObject<BlockEntityType<CopperGlowCampfireBlockEntity>> COPPER_GLOW_CAMPFIRE = BLOCK_ENTITIES.register(
            "copper_glow_campfire",
            () -> BlockEntityType.Builder.of(CopperGlowCampfireBlockEntity::new,
                                            ModBlocks.COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.EXPOSED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WEATHERED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.OXIDIZED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE.get(),

                                            ModBlocks.WAXED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_EXPOSED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_WEATHERED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_EXPOSED_CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_WEATHERED_CUT_COPPER_GLOW_CAMPFIRE.get(),
                                            ModBlocks.WAXED_OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE.get()
            ).build(null)
    );

    // Make the registry known to the event bus.
    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}