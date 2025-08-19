package net.thebrewingminer.glowlights.init;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;

public class ModParticles {
    public static final DeferredRegister<ParticleType<?>> PARTICLE_TYPES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, GlowLights.MOD_ID);

    public static final RegistryObject<SimpleParticleType> COZY_GLOW_SMOKE = PARTICLE_TYPES.register(
        "cozy_glow_smoke",
        () -> new SimpleParticleType(true)
    );

    public static final RegistryObject<SimpleParticleType> SIGNAL_GLOW_SMOKE = PARTICLE_TYPES.register(
        "signal_glow_smoke",
        () -> new SimpleParticleType(true)
    );

    public static void register(IEventBus eventBus){
        PARTICLE_TYPES.register(eventBus);
    }
}