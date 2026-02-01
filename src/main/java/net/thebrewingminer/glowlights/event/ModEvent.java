package net.thebrewingminer.glowlights.event;

import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.init.ModParticles;
import net.thebrewingminer.glowlights.particle.GlowSmokeParticle;

@Mod.EventBusSubscriber(modid = GlowLights.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEvent {

    // Register particles for the client to use
    @SubscribeEvent
    public static void registerParticles(RegisterParticleProvidersEvent particleProvidersEvent){
        particleProvidersEvent.registerSpriteSet(ModParticles.COZY_GLOW_SMOKE.get(), GlowSmokeParticle.CosyGlowProvider::new);
        particleProvidersEvent.registerSpriteSet(ModParticles.SIGNAL_GLOW_SMOKE.get(), GlowSmokeParticle.GlowSignalProvider::new);
    }
}