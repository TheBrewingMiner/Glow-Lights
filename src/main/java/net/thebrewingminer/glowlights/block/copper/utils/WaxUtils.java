package net.thebrewingminer.glowlights.block.copper.utils;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public final class WaxUtils {
    private WaxUtils(){}

    public static void triggerOnHoneycomb(Level level, Player player, BlockPos pos, ItemStack heldItem){
        if (!level.isClientSide() && player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.ITEM_USED_ON_BLOCK.trigger(serverPlayer, pos, heldItem);
        }
    }
}