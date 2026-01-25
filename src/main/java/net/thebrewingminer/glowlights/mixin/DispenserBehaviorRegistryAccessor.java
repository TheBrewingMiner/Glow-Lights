package net.thebrewingminer.glowlights.mixin;

import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(DispenserBlock.class)
public abstract class DispenserBehaviorRegistryAccessor {

    // Define and inject a getter for package-private DISPENSER_REGISTRY
    @Accessor("DISPENSER_REGISTRY")
    public static Map<Item, DispenseItemBehavior> getDispenserRegistry() {
        throw new AssertionError();
    }
}