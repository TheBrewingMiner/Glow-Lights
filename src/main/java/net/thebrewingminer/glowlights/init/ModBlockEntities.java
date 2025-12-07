package net.thebrewingminer.glowlights.init;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.entity.GlowCampfireBlockEntity;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, GlowLights.MOD_ID);

    public static final RegistryObject<BlockEntityType<GlowCampfireBlockEntity>> GLOW_CAMPFIRE = BLOCK_ENTITIES.register(
            "glow_campfire",
            () -> BlockEntityType.Builder.of(GlowCampfireBlockEntity::new,
                                            ModBlocks.PRISMARINE_GLOW_CAMPFIRE.get(),
                                            ModBlocks.PRISMARINE_BRICK_GLOW_CAMPFIRE.get(),
                                            ModBlocks.DARK_PRISMARINE_GLOW_CAMPFIRE.get()
            ).build(null)
    );

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}