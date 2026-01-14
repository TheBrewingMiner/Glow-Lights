package net.thebrewingminer.glowlights.mixin;

import net.minecraft.world.level.block.CampfireBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(CampfireBlock.class)
public class CampfireCanLightMixin {

    // Injects the following method into the beginning of CampfireBlock.canLight(BlockState).
    // Adds a check for glow campfires before checking vanilla campfires.
    // This is to ensure that any code that uses Vanilla-aligned logic for lighter items
    // respect the custom logic for glow campfires without having to manually edit them.

    @Inject(method = "canLight", at = @At("HEAD"), cancellable = true)
    private static void glowlights$canLight(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (GlowCampfireUtils.isGlowCampfire(state)) {
            cir.setReturnValue(GlowCampfireUtils.canLight(state));
        }
    }
}
