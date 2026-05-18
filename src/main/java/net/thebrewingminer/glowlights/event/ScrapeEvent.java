package net.thebrewingminer.glowlights.event;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ToolActions;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.thebrewingminer.glowlights.GlowLights;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;

@Mod.EventBusSubscriber(modid = GlowLights.MOD_ID)
public class ScrapeEvent {
    // It seems that in Forge 1.20.3, the event is broadcasted for axes ONLY when the tool action is AXE_STRIP.

    // Handle scraping for custom copper when an axe is used on it.
    @SubscribeEvent
    public static void scrapeOxidation(BlockEvent.BlockToolModificationEvent toolEvent){
        UseOnContext useOnContext = toolEvent.getContext();
        ItemStack heldItem = useOnContext.getItemInHand();

        BlockState blockState = toolEvent.getState();
        Block block = blockState.getBlock();

        boolean isCopper = (block instanceof IWeatheringCopper);
        boolean usingAxe = heldItem.is(ItemTags.AXES);
        boolean isScrapingOff = (toolEvent.getToolAction() == ToolActions.AXE_STRIP);

        if (!isCopper) return;
        if (!usingAxe) return;
        if (!isScrapingOff) return;

        IWeatheringCopper.getPrevious(block).ifPresent(scraped -> {
            toolEvent.setFinalState(scraped.withPropertiesOf(blockState));
            if (!toolEvent.isSimulated()) {
                useOnContext.getLevel().playSound(
                        useOnContext.getPlayer(), useOnContext.getClickedPos(),
                        SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F
                );
                useOnContext.getLevel().levelEvent(useOnContext.getPlayer(), 3005, useOnContext.getClickedPos(), 0);
            }
        });
    }

    // Handle scraping wax for custom waxed copper when an axe is used on it.
    @SubscribeEvent
    public static void scrapeWax(BlockEvent.BlockToolModificationEvent toolEvent){
        UseOnContext useOnContext = toolEvent.getContext();
        ItemStack heldItem = useOnContext.getItemInHand();

        BlockState blockState = toolEvent.getState();
        Block block = blockState.getBlock();

        boolean usingAxe = heldItem.is(ItemTags.AXES);
        boolean isWaxingOff = (toolEvent.getToolAction() == ToolActions.AXE_STRIP);

        if (!usingAxe) return;
        if (!isWaxingOff) return;

        IWeatheringCopper.getUnwaxed(block).ifPresent(scraped -> {
            toolEvent.setFinalState(scraped.withPropertiesOf(blockState));
            if (!toolEvent.isSimulated()) {
                useOnContext.getLevel().playSound(useOnContext.getPlayer(), useOnContext.getClickedPos(), SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                useOnContext.getLevel().levelEvent(useOnContext.getPlayer(), 3004, useOnContext.getClickedPos(), 0);
            }
        });
    }
}