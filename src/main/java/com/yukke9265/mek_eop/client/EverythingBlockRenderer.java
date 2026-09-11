package com.yukke9265.mek_eop.client;

import org.jetbrains.annotations.Nullable;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.yukke9265.mek_eop.block.EverythingBlockEntity;
import com.yukke9265.mek_eop.component.OriginalItem;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * なんでもブロックの世界描画。
 * <p>
 * 通常モデル（ティント付き立方体）の上に、元アイテムのアイコンを各面の中央へ少し小さく重ねる。
 * 同じ面貼り付けは手持ち・GUI（BEWLR）からも {@link #renderOriginOnFaces} で使う。
 */
public class EverythingBlockRenderer implements BlockEntityRenderer<EverythingBlockEntity> {

    /** 面に対するアイコンの大きさ（ブロック辺に対する比率）。 */
    private static final float ORIGIN_SCALE = 0.5f;
    /**
     * scale 後にアイテム中心を面から外へ出す量（render の -0.5 ずらしで厚み半分が内側に食うのを相殺）。
     * 以前は 0.51（≒0.25 ブロック相当）で飛び出し過ぎだったので、表面すれすれの隙間だけ残す。
     */
    private static final float OUTWARD_AFTER_SCALE = 0.02f;

    public EverythingBlockRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(EverythingBlockEntity blockEntity, float partialTick, PoseStack poseStack,
            MultiBufferSource buffer, int packedLight, int packedOverlay) {
        OriginalItem origin = blockEntity.getOrigin();
        if (origin == null) {
            return;
        }
        renderOriginOnFaces(origin.original(), poseStack, buffer, packedLight, packedOverlay, blockEntity.getLevel());
    }

    /**
     * 立方体の 6 面に元アイテムアイコンを貼る。
     * <p>
     * 前提: pose はブロックの角が (0,0,0)、辺の長さが 1 のローカル座標（世界の BER と同じ）。
     * <p>
     * ItemRenderer.render(..., NONE) は内部で (-0.5,-0.5,-0.5) ずらすので、アイテムは原点中心になる。
     * そのまま面の位置に置くと厚みの半分が立方体に埋まり真っ黒に見える → 面の上で scale したあと、さらに外側へ半分ずらす。
     */
    public static void renderOriginOnFaces(ItemStack original, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, @Nullable Level level) {
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
        BakedModel originModel = itemRenderer.getModel(original, level, null, 0);
        // 面デカルなのでブロックの影で潰れないよう最大明るさ
        int light = LightTexture.FULL_BRIGHT;

        for (Direction face : Direction.values()) {
            poseStack.pushPose();
            poseStack.translate(0.5f, 0.5f, 0.5f);
            poseStack.mulPose(face.getRotation());
            // getRotation 後は +Y が面の外側。アイテムの表が +Z を向くよう -90°X
            poseStack.mulPose(Axis.XP.rotationDegrees(-90.0f));
            // 表が外側を向く（無いと裏面＝暗い面が見える）
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f));
            // 立方体の面の位置へ（中心から 0.5）
            poseStack.translate(0.0f, 0.0f, 0.5f);
            poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
            // 中心化されたアイテムの背面が面に来るよう、厚み半分 + わずかな隙間だけ外へ
            poseStack.translate(0.0f, 0.0f, OUTWARD_AFTER_SCALE);
            itemRenderer.render(original, ItemDisplayContext.NONE, false, poseStack, buffer, light, packedOverlay, originModel);
            poseStack.popPose();
        }
    }
}
