package net.thebrewingminer.glowlights.init;

import net.minecraft.world.item.*;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.thebrewingminer.glowlights.GlowLights;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, GlowLights.MOD_ID);

    /* Items */
    public static final RegistryObject<Item> GLOW_TORCH = ITEMS.register(
        "glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.GLOW_TORCH.get(),
            ModBlocks.GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

        /* Copper */

    public static final RegistryObject<Item> COPPER_GLOW_TORCH = ITEMS.register(
        "copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.COPPER_GLOW_TORCH.get(),
            ModBlocks.COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> EXPOSED_COPPER_GLOW_TORCH = ITEMS.register(
        "exposed_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.EXPOSED_COPPER_GLOW_TORCH.get(),
            ModBlocks.EXPOSED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> WEATHERED_COPPER_GLOW_TORCH = ITEMS.register(
        "weathered_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.WEATHERED_COPPER_GLOW_TORCH.get(),
            ModBlocks.WEATHERED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> OXIDIZED_COPPER_GLOW_TORCH = ITEMS.register(
        "oxidized_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.OXIDIZED_COPPER_GLOW_TORCH.get(),
            ModBlocks.OXIDIZED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    /* Waxed Copper Items */

    public static final RegistryObject<Item> WAXED_COPPER_GLOW_TORCH = ITEMS.register(
        "waxed_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.WAXED_COPPER_GLOW_TORCH.get(),
            ModBlocks.WAXED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> WAXED_EXPOSED_COPPER_GLOW_TORCH = ITEMS.register(
        "waxed_exposed_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.WAXED_EXPOSED_COPPER_GLOW_TORCH.get(),
            ModBlocks.WAXED_EXPOSED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> WAXED_WEATHERED_COPPER_GLOW_TORCH = ITEMS.register(
        "waxed_weathered_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.WAXED_WEATHERED_COPPER_GLOW_TORCH.get(),
            ModBlocks.WAXED_WEATHERED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static final RegistryObject<Item> WAXED_OXIDIZED_COPPER_GLOW_TORCH = ITEMS.register(
        "waxed_oxidized_copper_glow_torch",
        () -> new StandingAndWallBlockItem(
            ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_TORCH.get(),
            ModBlocks.WAXED_OXIDIZED_COPPER_GLOW_WALL_TORCH.get(),
            new Item.Properties().tab(CreativeModeTab.TAB_DECORATIONS)
        )
    );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}