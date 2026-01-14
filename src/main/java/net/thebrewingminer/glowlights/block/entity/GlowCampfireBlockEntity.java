package net.thebrewingminer.glowlights.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEvent.Context;
import net.thebrewingminer.glowlights.block.GlowCampfireBlock;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.init.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.Optional;

public class GlowCampfireBlockEntity extends BlockEntity implements Clearable {
    protected static final int BURN_COOL_SPEED = 2;
    protected static final int NUM_SLOTS = 4;
    protected final NonNullList<ItemStack> items;
    protected final int[] cookingProgress;
    protected final int[] cookingTime;
    protected final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck;

    public static final int SMOKE_DELAY = 20;

    // CampfireBlockEntity-like object for GlowCampfireBlock.
    public GlowCampfireBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.GLOW_CAMPFIRE.get(), pos, blockState);
        this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        this.cookingProgress = new int[NUM_SLOTS];
        this.cookingTime = new int[NUM_SLOTS];
        this.quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    }

    // Processes cooking for lit campfires.
    public static void cookTick(Level level, BlockPos pos, BlockState blockState, GlowCampfireBlockEntity blockEntity) {
        boolean flag = false;

        // Check if the campfire has the ash-when-unlit property while being lit (which by design is meant to be an illegal state).
        // If both properties are true, set HAS_ASH_UNLIT to false.
        if (GlowCampfireUtils.hasAshWhileUnlit(blockState)){ level.setBlock(pos, GlowCampfireUtils.litFromAsh(blockState), 3); }

        for(int itemOnCampfire = 0; itemOnCampfire < blockEntity.getItems().size(); ++itemOnCampfire) {
            ItemStack itemStack = blockEntity.getItems().get(itemOnCampfire);
            if (!itemStack.isEmpty()) {
                flag = true;
                blockEntity.cookingProgress[itemOnCampfire]++;

                if (blockEntity.cookingProgress[itemOnCampfire] >= blockEntity.cookingTime[itemOnCampfire]) {
                    Container container = new SimpleContainer(itemStack);
                    ItemStack stack = blockEntity.quickCheck.getRecipeFor(container, level).map((campfireCookingRecipe) -> campfireCookingRecipe.assemble(container)).orElse(itemStack);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    blockEntity.items.set(itemOnCampfire, ItemStack.EMPTY);
                    level.sendBlockUpdated(pos, blockState, blockState, 3);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, Context.of(blockState));
                }
            }
        }

        if (flag) {
            setChanged(level, pos, blockState);
        }

    }

    // Processes cooling from any remaining cookTick logic when the campfire is unlit.
    public static void cooldownTick(Level level, BlockPos pos, BlockState blockState, GlowCampfireBlockEntity blockEntity) {
        boolean flag = false;

        for(int itemIndex = 0; itemIndex < blockEntity.items.size(); ++itemIndex) {
            if (blockEntity.cookingProgress[itemIndex] > 0) {
                flag = true;
                blockEntity.cookingProgress[itemIndex] = Mth.clamp(blockEntity.cookingProgress[itemIndex] - BURN_COOL_SPEED, 0, blockEntity.cookingTime[itemIndex]);
            }
        }

        if (flag) { setChanged(level, pos, blockState); }
    }

    // Makes particles for the client to render.
    public static void particleTick(Level level, BlockPos pos, BlockState blockState, GlowCampfireBlockEntity blockEntity) {
        RandomSource randomSource = level.random;
        int i;
        int particleAmount = blockState.getValue(BlockStateProperties.WATERLOGGED) ? randomSource.nextInt(2) + 2 : randomSource.nextInt(2);
        if (randomSource.nextFloat() < 0.11F) {
            for(i = 0; i < particleAmount; ++i) {
                GlowCampfireBlock.makeParticles(level, pos, blockState.getValue(GlowCampfireBlock.SIGNAL_FIRE), false);
            }
        }

        i = blockState.getValue(GlowCampfireBlock.FACING).get2DDataValue();

        for(int itemIndex = 0; itemIndex < blockEntity.items.size(); ++itemIndex) {
            if (!blockEntity.items.get(itemIndex).isEmpty() && randomSource.nextFloat() < 0.2F) {
                Direction direction = Direction.from2DDataValue(Math.floorMod(itemIndex + i, NUM_SLOTS));
                float factor = 0.3125F;
                double x = (double)pos.getX() + 0.5 - (double)((float)direction.getStepX() * 0.3125F) + (double)((float)direction.getClockWise().getStepX() * factor);
                double y = (double)pos.getY() + 0.5;
                double z = (double)pos.getZ() + 0.5 - (double)((float)direction.getStepZ() * 0.3125F) + (double)((float)direction.getClockWise().getStepZ() * factor);

                for (int j = 0; j < NUM_SLOTS; ++j) {
                    if (randomSource.nextInt(SMOKE_DELAY) == 0) {
                        level.addParticle(ParticleTypes.GLOW, x, y, z, 0.0, 5.0E-4, 0.0);
                    } else if (randomSource.nextInt(SMOKE_DELAY) <= 5){
                        level.addParticle(ParticleTypes.SMOKE, x, y, z, 0.0, 5.0E-4, 0.0);
                    }
                }
            }
        }
    }

    // Gets stored items.
    public NonNullList<ItemStack> getItems() {
        return this.items;
    }

    // Loads information from saved data.
    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        this.items.clear();
        ContainerHelper.loadAllItems(compoundTag, this.items);
        int[] $$2;
        if (compoundTag.contains("CookingTimes", 11)) {
            $$2 = compoundTag.getIntArray("CookingTimes");
            System.arraycopy($$2, 0, this.cookingProgress, 0, Math.min(this.cookingTime.length, $$2.length));
        }

        if (compoundTag.contains("CookingTotalTimes", 11)) {
            $$2 = compoundTag.getIntArray("CookingTotalTimes");
            System.arraycopy($$2, 0, this.cookingTime, 0, Math.min(this.cookingTime.length, $$2.length));
        }

    }

    // Saves data.
    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        ContainerHelper.saveAllItems(compoundTag, this.items, true);
        compoundTag.putIntArray("CookingTimes", this.cookingProgress);
        compoundTag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true);
        return tag;
    }

    // Gets the recipe for the passed-in item stack if it exists.
    public Optional<CampfireCookingRecipe> getCookableRecipe(ItemStack itemStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.quickCheck.getRecipeFor(new SimpleContainer(itemStack), this.level);
    }

    // Processes the player placing food into the campfire. Returns true if it succeeds.
    public boolean placeFood(@Nullable Entity entity, ItemStack itemStack, int cookTime) {
        for(int itemIndex = 0; itemIndex < this.items.size(); ++itemIndex) {
            ItemStack item = this.items.get(itemIndex);
            if (item.isEmpty()) {
                this.cookingTime[itemIndex] = cookTime;
                this.cookingProgress[itemIndex] = 0;
                this.items.set(itemIndex, itemStack.split(1));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), Context.of(entity, this.getBlockState()));
                this.markUpdated();
                return true;
            }
        }

        return false;
    }

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    @Override
    public void clearContent() {
        this.items.clear();
    }

    public void dowse() {
        if (this.level != null) { this.markUpdated(); }
    }
}