package net.thebrewingminer.glowlights;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.thebrewingminer.glowlights.advancements.ModCriteriaTriggers;
import net.thebrewingminer.glowlights.block.dispenser.GlowCampfireDispenserBehavior;
import net.thebrewingminer.glowlights.init.ModBlocks;
import net.thebrewingminer.glowlights.init.ModBlockEntities;
import net.thebrewingminer.glowlights.init.ModItems;
import net.thebrewingminer.glowlights.init.ModParticles;

@Mod(GlowLights.MOD_ID)
public class GlowLights {
    public static final String MOD_ID = "glowlights";

    public GlowLights() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register all registries.
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModParticles.register(modEventBus);
        ModCriteriaTriggers.init();

        modEventBus.addListener(this::onCommonSetup);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GlowCampfireDispenserBehavior::register); // Register custom dispenser behavior.
    }
}