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

    // Copies Vanilla logic and makes it apply to GlowLights' custom copper blocks, then injects it
    // at the end of LightningBolt.cleanCopperOnLightningStrike(Level, BlockPos).
    // It also adds direct-lightning-strike lighting of any glow campfire.

    // While the custom copper blocks are still instances of Vanilla's WeatheringCopper, their oxidation
    // and waxing relationships are stored in custom block maps that the original method does not call.

    // The logic then runs parallel to Vanilla's logic, giving the in-game appearance of them working together.
    // They do not overlap because Vanilla's copper block maps and GlowLights' block maps are disjoint— don't overlap—
    // and while IWeatheringCopper is instanceof WeatheringCopper, WeatheringCopper is not instanceof IWeatheringCopper.

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
        if (GlowCampfireUtils.isGlowCampfire(targetState) && GlowCampfireUtils.hasAshWhileUnlit(targetState)) {
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