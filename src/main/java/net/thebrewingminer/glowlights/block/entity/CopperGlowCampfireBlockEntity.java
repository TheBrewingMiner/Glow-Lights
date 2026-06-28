package net.thebrewingminer.glowlights.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.thebrewingminer.glowlights.init.ModCriteriaTriggers;
import net.thebrewingminer.glowlights.block.copper.CopperGlowCampfireBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;
import net.thebrewingminer.glowlights.block.copper.utils.WeatheringBlockMap;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.init.ModBlockEntities;

import javax.annotation.Nullable;
import java.util.Optional;

@SuppressWarnings({"NullableProblems"})
public class CopperGlowCampfireBlockEntity extends BlockEntity implements Clearable {
    protected static final int NUM_SLOTS = 4;
    protected final NonNullList<ItemStack> items;
    protected final int[] cookingProgress;
    protected final int[] cookingTime;
    protected final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);

    protected static int floatToIntScale = 100;
    public static final int SMOKE_DELAY = 20;

    // CampfireBlockEntity-like object for CopperGlowCampfireBlock.
    public CopperGlowCampfireBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlockEntities.COPPER_GLOW_CAMPFIRE.get(), pos, blockState);
        this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        this.cookingProgress = new int[NUM_SLOTS];
        this.cookingTime = new int[NUM_SLOTS];
    }

    // Gets the weather state (enum) from the copper block at the block-entity's position.
    // If the block is a waxed version, get the information from WAX_OFF_BY_BLOCK map.
    public static WeatheringCopper.WeatherState getWeatherState(Block block) {
        if (block instanceof IWeatheringCopper copper) return copper.getAge();

        Block unwaxed = WeatheringBlockMap.WAX_OFF_BY_BLOCK.get().get(block);
        if (unwaxed instanceof IWeatheringCopper copper) return copper.getAge();

        return WeatheringCopper.WeatherState.UNAFFECTED;
    }

    // Calculates a bonus/penalty as a float based on a copper weather state.
    @SuppressWarnings("DataFlowIssue")
    public static float getCookSpeed(BlockState blockState){
        float cookSpeed = 1.00f;
        WeatheringCopper.WeatherState age = getWeatherState(blockState.getBlock());

        if (age == WeatheringCopper.WeatherState.UNAFFECTED) { cookSpeed = 1.50f; }
        else if (age == WeatheringCopper.WeatherState.EXPOSED) { cookSpeed = 1.25f; }
        else if (age == WeatheringCopper.WeatherState.WEATHERED) { cookSpeed = 1.00f; }
        else if (age == WeatheringCopper.WeatherState.OXIDIZED ) { cookSpeed = 0.50f; }

        return cookSpeed;
    }

    // Processes cooking for lit campfires.
    public static void cookTick(Level level, BlockPos pos, BlockState blockState, CopperGlowCampfireBlockEntity blockEntity) {
        boolean flag = false;

        // Check if the campfire has the ash-while-unlit property while being lit (which by design is meant to be an illegal state).
        // If both properties are true, set HAS_ASH_UNLIT to false.
        if (GlowCampfireUtils.hasAshWhileUnlit(blockState)){ level.setBlock(pos, GlowCampfireUtils.litFromAsh(blockState), 3); }

        for(int itemOnCampfire = 0; itemOnCampfire < blockEntity.getItems().size(); ++itemOnCampfire) {
            ItemStack itemStack = blockEntity.getItems().get(itemOnCampfire);
            if (!itemStack.isEmpty()) {
                flag = true;
                float cookSpeed = getCookSpeed(blockState); // Calculate cook speed based off of weather state of the block.
                int scaledCookSpeed = (int)(cookSpeed * floatToIntScale);   // Scale the cook speed into an integer value for later serialization.
                blockEntity.cookingProgress[itemOnCampfire] += scaledCookSpeed;

                if (blockEntity.cookingProgress[itemOnCampfire] >= (blockEntity.cookingTime[itemOnCampfire] * floatToIntScale)) {
                    SingleRecipeInput singleRecipeInput = new SingleRecipeInput(itemStack);
//                    ItemStack stack = blockEntity.quickCheck.getRecipeFor(container, level).map((campfireCookingRecipeHolder) -> campfireCookingRecipeHolder.value().assemble(container, level.registryAccess())).orElse(itemStack);
                    ItemStack stack = blockEntity.quickCheck
                            .getRecipeFor(singleRecipeInput, level)
                            .map(recipeHolder -> recipeHolder.value().assemble(singleRecipeInput, level.registryAccess()))
                            .orElse(itemStack);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    blockEntity.items.set(itemOnCampfire, ItemStack.EMPTY);

                    double i = pos.getX();
                    double j = pos.getY();
                    double k = pos.getZ();

                    for (ServerPlayer serverPlayer : level.getEntitiesOfClass(ServerPlayer.class, (new AABB(i, j, k, i, j, k).inflate(10.0D, 5.0D, 10.0D)))) {
                        ModCriteriaTriggers.GLOW_CAMPFIRE_COOKED_TRIGGER.trigger(serverPlayer, pos);
                    }

                    level.sendBlockUpdated(pos, blockState, blockState, 3);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                }
            }
        }

        if (flag) {
            setChanged(level, pos, blockState);
        }

    }

    // Processes cooling from any remaining cookTick logic when the campfire is unlit.
    public static void cooldownTick(Level level, BlockPos pos, BlockState blockState, CopperGlowCampfireBlockEntity blockEntity) {
        boolean flag = false;
        float coolSpeed = getCookSpeed(blockState); // Get the cool down speed (equal to cook speed for copper campfires).
        int scaledCoolSpeed = (int)(coolSpeed * floatToIntScale);   // Scale the cool speed to an integer value for later serialization.

        for(int itemIndex = 0; itemIndex < blockEntity.items.size(); ++itemIndex) {
            if (blockEntity.cookingProgress[itemIndex] > 0) {
                flag = true;
                blockEntity.cookingProgress[itemIndex] = Mth.clamp((blockEntity.cookingProgress[itemIndex] - scaledCoolSpeed), 0, blockEntity.cookingTime[itemIndex] * floatToIntScale);
            }
        }

        if (flag) { setChanged(level, pos, blockState); }
    }

    // Makes particles for the client to render.
    public static void particleTick(Level level, BlockPos pos, BlockState blockState, CopperGlowCampfireBlockEntity blockEntity) {
        RandomSource randomSource = level.random;
        int i;
        int particleAmount = blockState.getValue(BlockStateProperties.WATERLOGGED) ? randomSource.nextInt(2) + 2 : randomSource.nextInt(2);
        if (randomSource.nextFloat() < 0.11F) {
            for(i = 0; i < particleAmount; ++i) {
                CopperGlowCampfireBlock.makeParticles(level, pos, blockState.getValue(CopperGlowCampfireBlock.SIGNAL_FIRE), false);
            }
        }

        i = blockState.getValue(CopperGlowCampfireBlock.FACING).get2DDataValue();

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
    public void loadAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.loadAdditional(compoundTag, lookupProvider);
        this.items.clear();
        ContainerHelper.loadAllItems(compoundTag, this.items, lookupProvider);
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
    protected void saveAdditional(CompoundTag compoundTag, HolderLookup.Provider lookupProvider) {
        super.saveAdditional(compoundTag, lookupProvider);
        ContainerHelper.saveAllItems(compoundTag, this.items, true, lookupProvider);
        compoundTag.putIntArray("CookingTimes", this.cookingProgress);
        compoundTag.putIntArray("CookingTotalTimes", this.cookingTime);
    }

    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider lookupProvider) {
        CompoundTag tag = new CompoundTag();
        ContainerHelper.saveAllItems(tag, this.items, true, lookupProvider);
        return tag;
    }

    // Gets the recipe for the passed-in item stack if it exists.
    public Optional<RecipeHolder<CampfireCookingRecipe>> getCookableRecipe(ItemStack itemStack) {
        return this.items.stream().noneMatch(ItemStack::isEmpty) ? Optional.empty() : this.quickCheck.getRecipeFor(new SingleRecipeInput(itemStack), this.level);
    }

    // Processes the player placing food into the campfire. Returns true if it succeeds.
    public boolean placeFood(@Nullable Entity entity, ItemStack itemStack, int cookTime) {
        for(int itemIndex = 0; itemIndex < this.items.size(); ++itemIndex) {
            ItemStack item = this.items.get(itemIndex);
            if (item.isEmpty()) {
                this.cookingTime[itemIndex] = cookTime;
                this.cookingProgress[itemIndex] = 0;
                this.items.set(itemIndex, itemStack.split(1));
                this.level.gameEvent(GameEvent.BLOCK_CHANGE, this.getBlockPos(), GameEvent.Context.of(entity, this.getBlockState()));
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