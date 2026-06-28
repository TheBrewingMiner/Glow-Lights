package net.thebrewingminer.glowlights.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.mixin.DispenserBehaviorRegistryAccessor;

import java.util.Map;

@SuppressWarnings({"NullableProblems"})
public final class GlowCampfireDispenserBehavior {
    private GlowCampfireDispenserBehavior(){}

    public static void register() {

        // Accessor mixin allows getting the dispenser behavior registry (which is package-private).
        Map<Item, DispenseItemBehavior> registry = DispenserBehaviorRegistryAccessor.getDispenserRegistry();

        // Gets the value of the key Items.FLINT_AND_STEEL in the map,
        // which is the registered behavior for flint and steel by common setup.
        DispenseItemBehavior vanillaBehavior = registry.get(Items.FLINT_AND_STEEL);

        // Reregister behavior for Items.FLINT_AND_STEEL, which replaces the behavior previously in the registry.
        DispenserBlock.registerBehavior(
            Items.FLINT_AND_STEEL,  // The key of this mapped relationship
            new OptionalDispenseItemBehavior() {    // Anonymous class that dictates what the dispenser does when it dispenses the item of the key

                @Override
                protected ItemStack execute(BlockSource source, ItemStack itemStack) {
                    ServerLevel level = source.level();
                    Direction facing = source.state().getValue(DispenserBlock.FACING);
                    BlockPos pos = source.pos().relative(facing);
                    BlockState state = level.getBlockState(pos);

                    // Check if the block in front of the dispenser is a glow campfire
                    if (GlowCampfireUtils.isGlowCampfire(state)) {
                        // Respect custom lighting logic for Glow Campfires.
                        if (GlowCampfireUtils.canLight(state)) {
                            // Light the campfire!
                            BlockState newState = GlowCampfireUtils.litFromAsh(state);
                            level.setBlock(pos, newState, 11);
                            level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);

                            this.setSuccess(true);

                        } else {
                            // Nothing happens.
                            this.setSuccess(false);
                        }

                        if (this.isSuccess()){ itemStack.hurtAndBreak(1, level, null, (item) -> {}); }

                        return itemStack;
                    }

                    // Otherwise, use the stored vanilla behavior we saved before rewriting the key
                    // to handle all other behaviors as usual.
                    return vanillaBehavior.dispense(source, itemStack);
                }
            }
        );
    }
}
