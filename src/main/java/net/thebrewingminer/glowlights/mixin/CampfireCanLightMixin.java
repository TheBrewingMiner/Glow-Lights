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

    @Inject(method = "canLight", at = @At("HEAD"), cancellable = true)
    private static void glowlights$canLight(BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (GlowCampfireUtils.isGlowCampfire(state)) {
            cir.setReturnValue(GlowCampfireUtils.canLight(state));
        }
    }
}
