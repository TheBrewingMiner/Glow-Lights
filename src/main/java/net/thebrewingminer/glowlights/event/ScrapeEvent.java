package net.thebrewingminer.glowlights.event;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
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
    public static void scrapeOxidation(BlockEvent.BlockToolModificationEvent toolEvent){
        UseOnContext useOnContext = toolEvent.getContext();
        Player player = useOnContext.getPlayer();
        ItemStack heldItem = useOnContext.getItemInHand();

        BlockState blockState = toolEvent.getState();
        BlockPos pos = useOnContext.getClickedPos();
        Block block = blockState.getBlock();

        if (!(block instanceof IWeatheringCopper)) return;
        if (!(heldItem.is(Tags.Items.TOOLS_AXES))) return;
        if (!(toolEvent.getToolAction() == ToolActions.AXE_SCRAPE)) return;

        IWeatheringCopper.getPrevious(block).ifPresent(scraped -> toolEvent.setFinalState(scraped.withPropertiesOf(blockState)));

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, heldItem);
        }
    }

    @SubscribeEvent
    public static void scrapeWax(BlockEvent.BlockToolModificationEvent toolEvent){
        UseOnContext useOnContext = toolEvent.getContext();
        Player player = useOnContext.getPlayer();
        ItemStack heldItem = useOnContext.getItemInHand();

        BlockState blockState = toolEvent.getState();
        BlockPos pos = useOnContext.getClickedPos();
        Block block = blockState.getBlock();

        if (!(heldItem.is(Tags.Items.TOOLS_AXES))) return;
        if (!(toolEvent.getToolAction() == ToolActions.AXE_WAX_OFF)) return;

        IWeatheringCopper.getUnwaxed(block).ifPresent(waxed -> toolEvent.setFinalState(waxed.withPropertiesOf(blockState)));

//        if (player instanceof ServerPlayer serverPlayer) {
//            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, heldItem);
//        }
    }
}