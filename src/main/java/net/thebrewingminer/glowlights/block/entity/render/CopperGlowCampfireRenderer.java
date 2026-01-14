package net.thebrewingminer.glowlights.block.entity.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.CampfireBlock;
import net.thebrewingminer.glowlights.block.entity.CopperGlowCampfireBlockEntity;

public class CopperGlowCampfireRenderer implements BlockEntityRenderer<CopperGlowCampfireBlockEntity> {
    private static final float SIZE = 0.375F;
    private final ItemRenderer itemRenderer;

    public CopperGlowCampfireRenderer(BlockEntityRendererProvider.Context context) {
        this.itemRenderer = context.getItemRenderer();
    }

    // Renders items on the copper glow campfire.
    public void render(CopperGlowCampfireBlockEntity pBlockEntity, float partialTick, PoseStack pPoseStack, MultiBufferSource multiBufferSource, int packedLight, int packedOverlay) {
        Direction direction = pBlockEntity.getBlockState().getValue(CampfireBlock.FACING);
        NonNullList<ItemStack> blockEntityItems = pBlockEntity.getItems();
        int longPos = (int)pBlockEntity.getBlockPos().asLong();

        for(int itemIndex = 0; itemIndex < blockEntityItems.size(); ++itemIndex) {
            ItemStack itemStack = blockEntityItems.get(itemIndex);
            if (itemStack != ItemStack.EMPTY) {
                pPoseStack.pushPose();
                pPoseStack.translate(0.5, 0.44921875, 0.5);
                Direction directionFrom2DDataValue = Direction.from2DDataValue((itemIndex + direction.get2DDataValue()) % 4);
                float toYRotation = -directionFrom2DDataValue.toYRot();
                pPoseStack.mulPose(Vector3f.YP.rotationDegrees(toYRotation));
                pPoseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
                pPoseStack.translate(-0.3125, -0.3125, 0.0);
                pPoseStack.scale(SIZE, SIZE, SIZE);
                this.itemRenderer.renderStatic(itemStack, ItemTransforms.TransformType.FIXED, packedLight, packedOverlay, pPoseStack, multiBufferSource, longPos + itemIndex);
                pPoseStack.popPose();
            }
        }
    }
}