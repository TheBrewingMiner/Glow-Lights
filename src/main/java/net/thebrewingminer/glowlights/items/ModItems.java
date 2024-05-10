package net.thebrewingminer.glowlights.items;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlowLights.MODID);

//  Item registered prior to my understanding of the code used within the ModBlocks class.
//    public static final RegistryObject<Item> GLOW_LANTERN = ITEMS.register("glow_lantern",
//            () -> new Item(new Item.Properties().tab(CreativeModeTab.TAB_BUILDING_BLOCKS)));

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}