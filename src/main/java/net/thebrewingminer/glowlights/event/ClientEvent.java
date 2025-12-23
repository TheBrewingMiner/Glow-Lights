package net.thebrewingminer.glowlights.event;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.entity.render.CopperGlowCampfireRenderer;
import net.thebrewingminer.glowlights.init.ModBlockEntities;
import net.thebrewingminer.glowlights.block.entity.render.GlowCampfireRenderer;

@Mod.EventBusSubscriber(modid = GlowLights.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvent {

    @SubscribeEvent
    public static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers renderersEvent){
        renderersEvent.registerBlockEntityRenderer(ModBlockEntities.GLOW_CAMPFIRE.get(), GlowCampfireRenderer::new);
        renderersEvent.registerBlockEntityRenderer(ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), CopperGlowCampfireRenderer::new);
    }
}