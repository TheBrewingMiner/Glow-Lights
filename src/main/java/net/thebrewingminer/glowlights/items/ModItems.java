package net.thebrewingminer.glowlights.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlowLights.MODID);

//  Item registered prior to my understanding of the code used within the ModBlocks class.

    public static final RegistryObject<Item> GLOW_TORCH = ITEMS.register("glow_torch",
            () -> new StandingAndWallBlockItem(
                    ModBlocks.GLOW_TORCH_BLOCK.get(),
                    ModBlocks.GLOW_WALL_TORCH_BLOCK.get(),
                    new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
            )
    );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}