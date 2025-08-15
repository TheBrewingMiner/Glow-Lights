package net.thebrewingminer.glowlights.event;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.custom.copper.utils.IWeatheringCopper;

@Mod.EventBusSubscriber(modid = GlowLights.MOD_ID)
public class ScrapeEvent {
    @SubscribeEvent
    public static void scrapeUnwaxed(BlockEvent.BlockToolModificationEvent toolEvent){
        UseOnContext useOnContext = toolEvent.getContext();
        ItemStack heldItem = useOnContext.getItemInHand();

        BlockState blockState = toolEvent.getState();
        Block block = blockState.getBlock();

        if (!(block instanceof IWeatheringCopper)) return;
        if (!(heldItem.is(Tags.Items.TOOLS_AXES))) return;
        if (!(toolEvent.getToolAction() == ToolActions.AXE_SCRAPE)) return;

        IWeatheringCopper.getPrevious(block).ifPresent(scraped -> toolEvent.setFinalState(scraped.withPropertiesOf(blockState)));
    }
}