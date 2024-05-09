package net.thebrewingminer.glowlights.block;

import net.minecraft.world.level.block.Block;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.thebrewingminer.glowlights.GlowLights;

public class ModBlocks {
    public static final DeferredRegister<Block> Blocks = DeferredRegister.create(ForgeRegistries.BLOCKS, GlowLights.MODID);



    public static void register(IEventBus eventBus) {
        Blocks.register(eventBus);
    }
}
