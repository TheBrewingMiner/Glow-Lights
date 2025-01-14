package net.thebrewingminer.glowlights.items;

import net.minecraft.core.Direction;
import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.ModBlocks;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlowLights.MODID);

    public static final RegistryObject<Item> GLOW_TORCH = ITEMS.register("glow_torch",
            () -> new StandingAndWallBlockItem(
                    ModBlocks.GLOW_TORCH_BLOCK.get(),
                    ModBlocks.GLOW_WALL_TORCH_BLOCK.get(),
                    Direction.DOWN, new Item.Properties())
    );

    public static final RegistryObject<Item> PRISMARINE_GLOW_CAMPFIRE = ITEMS.register("prismarine_glow_campfire",
            () -> new ItemNameBlockItem(ModBlocks.PRISMARINE_GLOW_CAMPFIRE.get(),
            new Item.Properties())
    );

    public static final RegistryObject<Item> PRISMARINE_BRICK_GLOW_CAMPFIRE = ITEMS.register("prismarine_brick_glow_campfire",
            () -> new ItemNameBlockItem(ModBlocks.PRISMARINE_BRICK_GLOW_CAMPFIRE.get(),
                    new Item.Properties())
    );

    public static final RegistryObject<Item> DARK_PRISMARINE_GLOW_CAMPFIRE = ITEMS.register("dark_prismarine_glow_campfire",
            () -> new ItemNameBlockItem(ModBlocks.DARK_PRISMARINE_GLOW_CAMPFIRE.get(),
                    new Item.Properties())
    );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}