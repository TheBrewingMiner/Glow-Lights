package net.thebrewingminer.glowlights.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.FireChargeItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.GlowCampfireBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FireChargeItem.class)
public class FireChargeItemMixin {

    @Inject(
        method = "useOn",
        at = @At("HEAD"),
        cancellable = true
    )
    private void preventGlowCampfireLighting(UseOnContext pContext, CallbackInfoReturnable<InteractionResult> cir){
        Level level = pContext.getLevel();
        BlockPos pos = pContext.getClickedPos();
        BlockState state = level.getBlockState(pos);

        if (state.getBlock() instanceof GlowCampfireBlock) cir.setReturnValue(InteractionResult.FAIL);
    }
}