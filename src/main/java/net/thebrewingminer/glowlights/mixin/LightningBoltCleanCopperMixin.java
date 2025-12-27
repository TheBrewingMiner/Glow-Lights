package net.thebrewingminer.glowlights.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LightningRodBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.thebrewingminer.glowlights.block.copper.utils.LightningUtils.randomWalkCleaningCustomCopper;

@Mixin(LightningBolt.class)
public class LightningBoltCleanCopperMixin {

    @Inject(
        method = "clearCopperOnLightningStrike",
        at = @At("TAIL")
    )
    private static void glowlights$cleanCustomCopper(Level level, BlockPos pos, CallbackInfo ci) {
        BlockState state = level.getBlockState(pos);

        // Handle lightning rods exactly like vanilla

        BlockPos targetPos;
        BlockState targetState;

        if (state.is(Blocks.LIGHTNING_ROD)) {
            targetPos = pos.relative(state.getValue(LightningRodBlock.FACING).getOpposite());
            targetState = level.getBlockState(targetPos);
        } else {
            targetPos = pos;
            targetState = state;
        }

        // Direct strike on any glow campfire
        if (GlowCampfireUtils.isGlowCampfire(targetState) && GlowCampfireUtils.hasAshWhenUnlit(targetState)) {
            level.setBlock(targetPos, GlowCampfireUtils.litFromAsh(targetState), 3);
        }

        Block block = targetState.getBlock();
        if (!(block instanceof WeatheringCopper)) return;

        // Complete deoxidization
        level.setBlockAndUpdate(targetPos, IWeatheringCopper.getFirst(targetState));

        // Variable deoxidization
        BlockPos.MutableBlockPos cursor = targetPos.mutable();
        int walks = level.random.nextInt(3) + 3;

        for (int i = 0; i < walks; i++) {
            int steps = level.random.nextInt(8) + 1;
            randomWalkCleaningCustomCopper(level, targetPos, cursor, steps);
        }
    }
}
