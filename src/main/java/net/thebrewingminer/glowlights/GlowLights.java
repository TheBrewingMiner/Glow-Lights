package net.thebrewingminer.glowlights;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.thebrewingminer.glowlights.init.ModCriteriaTriggers;
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
        modEventBus.addListener(this::addToCreativeTab);
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(GlowCampfireDispenserBehavior::register); // Register custom dispenser behavior.
    }

    private void addToCreativeTab(BuildCreativeModeTabContentsEvent event){
        ResourceKey<CreativeModeTab> tab = event.getTabKey();

        if (tab == CreativeModeTabs.BUILDING_BLOCKS){

            event.accept(ModBlocks.GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.GLOW_BERRY_LANTERN_BLOCK);

            event.accept(ModBlocks.COPPER_CHAIN);

            event.accept(ModBlocks.COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_LANTERN_BLOCK);

            event.accept(ModBlocks.COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK);


            event.accept(ModBlocks.WAXED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_LANTERN_BLOCK);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
        }

        if (tab == CreativeModeTabs.FUNCTIONAL_BLOCKS){

            event.accept(ModItems.GLOW_TORCH);
            event.accept(ModBlocks.GLOW_LANTERN);
            event.accept(ModBlocks.GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.GLOW_CAMPFIRE);
            event.accept(ModBlocks.PRISMARINE_GLOW_CAMPFIRE);
            event.accept(ModBlocks.PRISMARINE_BRICK_GLOW_CAMPFIRE);
            event.accept(ModBlocks.DARK_PRISMARINE_GLOW_CAMPFIRE);

            event.accept(ModBlocks.COPPER_CHAIN);

            event.accept(ModItems.COPPER_GLOW_TORCH);
            event.accept(ModItems.EXPOSED_COPPER_GLOW_TORCH);
            event.accept(ModItems.WEATHERED_COPPER_GLOW_TORCH);
            event.accept(ModItems.OXIDIZED_COPPER_GLOW_TORCH);

            event.accept(ModBlocks.COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_LANTERN);

            event.accept(ModBlocks.COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_BERRY_LANTERN);

            event.accept(ModBlocks.COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_LANTERN_BLOCK);

            event.accept(ModBlocks.COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK);

            event.accept(ModBlocks.COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.EXPOSED_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WEATHERED_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.OXIDIZED_COPPER_GLOW_CAMPFIRE);

            event.accept(ModBlocks.CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.EXPOSED_CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WEATHERED_CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE);


            event.accept(ModItems.WAXED_COPPER_GLOW_TORCH);
            event.accept(ModItems.WAXED_EXPOSED_COPPER_GLOW_TORCH);
            event.accept(ModItems.WAXED_WEATHERED_COPPER_GLOW_TORCH);
            event.accept(ModItems.WAXED_OXIDIZED_COPPER_GLOW_TORCH);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_LANTERN);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_LANTERN);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_BERRY_LANTERN);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_BERRY_LANTERN);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_LANTERN_BLOCK);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_BERRY_LANTERN_BLOCK);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_BERRY_LANTERN_BLOCK);

            event.accept(ModBlocks.WAXED_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_CAMPFIRE);

            event.accept(ModBlocks.WAXED_CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_EXPOSED_CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_WEATHERED_CUT_COPPER_GLOW_CAMPFIRE);
            event.accept(ModBlocks.WAXED_OXIDIZED_CUT_COPPER_GLOW_CAMPFIRE);
        }
    }
}