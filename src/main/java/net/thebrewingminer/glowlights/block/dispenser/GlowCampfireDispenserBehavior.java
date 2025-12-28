package net.thebrewingminer.glowlights.block.dispenser;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.mixin.DispenserBehaviorRegistryAccessor;

import java.util.Map;

public final class GlowCampfireDispenserBehavior {
    private GlowCampfireDispenserBehavior(){}

    public static void register() {

        Map<Item, DispenseItemBehavior> registry = DispenserBehaviorRegistryAccessor.getDispenserRegistry();
        DispenseItemBehavior vanillaBehavior = registry.get(Items.FLINT_AND_STEEL);

            DispenserBlock.registerBehavior(
                Items.FLINT_AND_STEEL,
                new OptionalDispenseItemBehavior() {

                    @Override
                    protected ItemStack execute(BlockSource source, ItemStack stack) {
                        Level level = source.getLevel();
                        Direction facing = source.getBlockState().getValue(DispenserBlock.FACING);
                        BlockPos pos = source.getPos().relative(facing);
                        BlockState state = level.getBlockState(pos);

                        if (GlowCampfireUtils.isGlowCampfire(state)) {
                            if (GlowCampfireUtils.canLight(state)) {
                                BlockState newState = GlowCampfireUtils.litFromAsh(state);
                                level.setBlock(pos, newState, 11);
                                level.gameEvent(null, GameEvent.BLOCK_CHANGE, pos);

                                if (stack.hurt(1, level.random, null)) { stack.setCount(0); }

                                this.setSuccess(true);
                            } else {
                                this.setSuccess(false);
                            }

                            return stack;
                        }

                        return vanillaBehavior.dispense(source, stack);
                    }
                }
            );
        System.out.println("Glowlights: Running DispenserBlock.registerBehavior()");
    }
}
