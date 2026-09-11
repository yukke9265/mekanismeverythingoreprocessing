package com.yukke9265.mek_eop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yukke9265.mek_eop.block.EverythingBlockEntity;
import com.yukke9265.mek_eop.component.OriginalItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * なんでもブロックの世界描画。
 * <p>
 * 通常モデル（ティント付き立方体）の上に、元アイテムのアイコンを各面の中央へ少し小さく重ねる。
 * アイテム版と同じ「中央に元アイテム」を、立方体の 6 面すべてに貼る実験。
 */
public class EverythingBlockRenderer implements BlockEntityRenderer<EverythingBlockEntity> {

    /** 面に対するアイコンの大きさ（ブロック辺に対する比率）。 */
    private static final float ORIGIN_SCALE = 0.5f;
    /** 面の少し外側に出して z-fighting を避ける。 */
    private static final float FACE_OFFSET = 0.501f;

    public EverythingBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EverythingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        OriginalItem origin = blockEntity.getOrigin();
        if (origin == null) {
            return;
        }
        ItemStack original = origin.original();
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        for (Direction face : Direction.values()) {
            poseStack.pushPose();
            // ブロック中心へ移動し、その面の外側が +Z になるよう回転する
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(face.getRotation());
            // getRotation 後は +Y が面の外側。FIXED アイテムは +Z 側が表なので -90°X で合わせる
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
            poseStack.translate(0.0f, 0.0f, FACE_OFFSET);
            poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
            itemRenderer.renderStatic(original, ItemDisplayContext.FIXED, packedLight, packedOverlay,
                    poseStack, buffer, blockEntity.getLevel(), 0);
            poseStack.popPose();
        }
    }
}
