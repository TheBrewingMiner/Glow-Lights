package net.thebrewingminer.glowlights.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.thebrewingminer.glowlights.block.copper.CopperGlowCampfireBlock;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;
import net.thebrewingminer.glowlights.block.entity.render.utils.RenderUtils;
import net.thebrewingminer.glowlights.block.utils.GlowCampfireUtils;
import net.thebrewingminer.glowlights.block.utils.GlowUtils;

public class CopperGlowCampfireRenderer implements BlockEntityRenderer<CopperGlowCampfireBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public CopperGlowCampfireRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    private static void animateFluidMovement(CopperGlowCampfireBlockEntity blockEntity, BlockState blockState, float partialTick, PoseStack poseStack, ItemStack itemStack, int itemIndex){
        if (!GlowUtils.isWaterlogged(blockState)) return;

        boolean lit = GlowCampfireUtils.isLit(blockState);

        long time = blockEntity.getLevel() != null ? blockEntity.getLevel().getGameTime() : 0;
        float partialTime = time + partialTick;

        long seed = blockEntity.getBlockPos().asLong()
                ^ (long)itemIndex * 31L
                ^ itemStack.getItem().hashCode();

        long mixedSeed = RenderUtils.mixSeed(seed);

        float energy = lit ? 1.30f : 1.0f;

        float phase = RenderUtils.unitFloat(mixedSeed, 0) * Mth.TWO_PI;
        float base_radius = RenderUtils.unitFloat(mixedSeed, 16) * 0.032f;
        float radius = lit ? base_radius * 1.4f : base_radius;
        float speed = 0.03f * energy;

        float bobSpeed = 0.04f * energy;
        float frequencyJitter = 0.85f + RenderUtils.unitFloat(mixedSeed, 48) * 0.3f;
        float amplitude = lit ? 0.02f : 0.015f;

        float x = Mth.cos(partialTime * speed + phase) * radius;
        float z = Mth.sin(partialTime * speed + phase) * radius;

        float raw_y = Mth.sin(partialTime * bobSpeed * frequencyJitter + phase) * amplitude;
        float y = Math.max(0.0f, raw_y);

        poseStack.translate(x, y, z);
    }

    // Renders items on the copper glow campfire.
    @SuppressWarnings("NullableProblems")
    public void render(CopperGlowCampfireBlockEntity blockEntity, float partialTick, PoseStack poseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay){
        Direction direction = blockEntity.getBlockState().getValue(CopperGlowCampfireBlock.FACING);
        BlockState blockState = blockEntity.getBlockState();
        NonNullList<ItemStack> items = blockEntity.getItems();
        int longPos = (int)blockEntity.getBlockPos().asLong();

        for (int itemIndex = 0; itemIndex < items.size(); itemIndex++){
            ItemStack itemStack = items.get(itemIndex);
            if (itemStack.isEmpty()) continue;

            poseStack.pushPose();
            poseStack.translate(0.5, 0.44921875, 0.5);

            // Change position slightly in time to simulate bobbing in water.
            animateFluidMovement(blockEntity, blockState, partialTick, poseStack, itemStack, itemIndex);

            Direction directionFrom2DDataValue = Direction.from2DDataValue((itemIndex + direction.get2DDataValue()) % 4);
            float toYRotation = -directionFrom2DDataValue.toYRot();
            poseStack.mulPose(Axis.YP.rotationDegrees(toYRotation));
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));

            poseStack.translate(-0.3125, -0.3125, 0.0);
            poseStack.scale(SIZE, SIZE, SIZE);

            this.itemRenderer.renderStatic(itemStack, ItemDisplayContext.FIXED, packedLight, packedOverlay, poseStack, multiBufferSource, blockEntity.getLevel(), longPos + itemIndex);
            poseStack.popPose();
        }
    }
}