package net.thebrewingminer.glowlights.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.GameEvent;
import net.thebrewingminer.glowlights.block.copper.CopperGlowCampfireBlock;
import net.thebrewingminer.glowlights.block.copper.utils.IWeatheringCopper;
import net.thebrewingminer.glowlights.block.copper.utils.WeatheringBlockMap;

public class CopperGlowCampfireBlockEntity extends GlowCampfireBlockEntity {
    protected static final int NUM_SLOTS = 4;
    protected final NonNullList<ItemStack> items;
    protected final int[] cookingProgress;
    protected final int[] cookingTime;
    protected final RecipeManager.CachedCheck<Container, CampfireCookingRecipe> quickCheck;

    public static final int SMOKE_DELAY = 20;

    public CopperGlowCampfireBlockEntity(BlockPos pos, BlockState blockState) {
        super(pos, blockState);
        this.items = NonNullList.withSize(NUM_SLOTS, ItemStack.EMPTY);
        this.cookingProgress = new int[NUM_SLOTS];
        this.cookingTime = new int[NUM_SLOTS];
        this.quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    }

    public static WeatheringCopper.WeatherState getWeatherState(Block block) {
        if (block instanceof IWeatheringCopper copper) return copper.getAge();

        Block unwaxed = WeatheringBlockMap.WAX_OFF_BY_BLOCK.get().get(block);
        if (unwaxed instanceof IWeatheringCopper copper) return copper.getAge();


        return WeatheringCopper.WeatherState.UNAFFECTED;
    }


    public static int getCookSpeed(BlockState blockState){
        int cookSpeed = 0;
        WeatheringCopper.WeatherState age = getWeatherState(blockState.getBlock());

        if (age == WeatheringCopper.WeatherState.UNAFFECTED) { cookSpeed = 3; }
        else if (age == WeatheringCopper.WeatherState.EXPOSED) { cookSpeed = 2; }
        else if (age == WeatheringCopper.WeatherState.WEATHERED) { cookSpeed = 1; }
        else if (age == WeatheringCopper.WeatherState.OXIDIZED ) { cookSpeed = 1; }

        return cookSpeed;
    }

    public static void cookTick(Level level, BlockPos pos, BlockState blockState, CopperGlowCampfireBlockEntity blockEntity) {
        boolean flag = false;

        for(int itemOnCampfire = 0; itemOnCampfire < blockEntity.getItems().size(); ++itemOnCampfire) {
            ItemStack itemStack = blockEntity.getItems().get(itemOnCampfire);
            if (!itemStack.isEmpty()) {
                flag = true;
//                blockEntity.cookingProgress[itemOnCampfire]++;
                int cookSpeed = getCookSpeed(blockState);
                blockEntity.cookingProgress[itemOnCampfire] += cookSpeed;


                if (blockEntity.cookingProgress[itemOnCampfire] >= blockEntity.cookingTime[itemOnCampfire]) {
                    Container container = new SimpleContainer(itemStack);
                    ItemStack stack = blockEntity.quickCheck.getRecipeFor(container, level).map((campfireCookingRecipe) -> campfireCookingRecipe.assemble(container)).orElse(itemStack);
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                    blockEntity.items.set(itemOnCampfire, ItemStack.EMPTY);
                    level.sendBlockUpdated(pos, blockState, blockState, 3);
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
                }
            }
        }

        if (flag) {
            setChanged(level, pos, blockState);
        }

    }

    public static void cooldownTick(Level level, BlockPos pos, BlockState blockState, CopperGlowCampfireBlockEntity blockEntity) {
        boolean flag = false;
        int coolSpeed = getCookSpeed(blockState);

        for(int itemIndex = 0; itemIndex < blockEntity.items.size(); ++itemIndex) {
            if (blockEntity.cookingProgress[itemIndex] > 0) {
                flag = true;
                blockEntity.cookingProgress[itemIndex] = Mth.clamp(blockEntity.cookingProgress[itemIndex] - coolSpeed, 0, blockEntity.cookingTime[itemIndex]);
            }
        }

        if (flag) { setChanged(level, pos, blockState); }
    }

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

    private void markUpdated() {
        this.setChanged();
        this.getLevel().sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }
}