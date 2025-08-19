package net.thebrewingminer.glowlights;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.thebrewingminer.glowlights.init.ModBlocks;
import net.thebrewingminer.glowlights.init.ModBlockEntities;
import net.thebrewingminer.glowlights.init.ModItems;

@Mod(GlowLights.MOD_ID)
public class GlowLights {
    public static final String MOD_ID = "glowlights";

    public GlowLights() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
    }
}