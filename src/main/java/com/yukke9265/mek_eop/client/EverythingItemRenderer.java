package com.yukke9265.mek_eop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * なんでも○○の描画。
 * <p>
 * 1. 見た目本体（{@code everything_x_base} モデル。ベース層は power 由来の色でティント）を通常描画する。
 * 2. 元アイテムがあれば、中央に元アイテムのアイコンを少し小さくして重ねる（GUI・手持ち・額縁など全て）。
 * <p>
 * 登録名のモデルは builtin/entity なので、ItemRenderer はこのクラスに描画を委ねてくる。
 */
public class EverythingItemRenderer extends BlockEntityWithoutLevelRenderer {

    /** 元アイテムアイコンの大きさ（本体に対する比率）。 */
    private static final float ORIGIN_SCALE = 0.7f;
    /** 平面アイテム: 本体（厚み 1/16）の手前に出すための法線方向オフセット。 */
    private static final float ORIGIN_Z_FLAT = 0.1f;
    /** ブロック: GUI で回転した立方体の手前に出すためのオフセット（GUI は平行投影なので大きくても見た目は変わらない）。 */
    private static final float ORIGIN_Z_BLOCK = 1.0f;

    public EverythingItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    /** アイテム登録名から見た目本体のモデル ID を作る。 */
    public static ModelResourceLocation baseModel(ItemStack stack) {
        String name = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return ModelResourceLocation.standalone(MekanismEverythingOreProcessing.rl("item/" + ModItems.baseModelPath(name)));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        // ここに来た時点で座標系は 0..1（ItemRenderer が -0.5 ずらした後）。中心を原点に戻す
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        // 1. 本体。ItemRenderer.render は内部で push/pop し、モデルの display 変換・-0.5 ずらし・ティントを適用してくれる
        BakedModel base = minecraft.getModelManager().getModel(baseModel(stack));
        itemRenderer.render(stack, context, false, poseStack, buffer, packedLight, packedOverlay, base);

        // 2. 元アイテムのアイコン
        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin != null) {
            if (stack.getItem() instanceof BlockItem) {
                renderOriginOnBlock(origin.original(), context, poseStack, buffer, packedLight, packedOverlay, itemRenderer);
            } else {
                renderOriginOnFlatItem(origin.original(), context, poseStack, buffer, packedLight, packedOverlay, itemRenderer, base);
            }
        }
        poseStack.popPose();
    }

    /**
     * 平面アイテム用: 本体と同じ display 変換を先に掛けてから、その座標系で法線方向に少しずらして描く。
     * <p>
     * こうすると手持ち・三人称・地面でも常に本体の「表面」にアイコンが乗る。
     * 変換は手動で掛けたので、元アイテム側は NONE（変換なし）で描画する。
     */
    private static void renderOriginOnFlatItem(ItemStack original, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, ItemRenderer itemRenderer, BakedModel base) {
        poseStack.pushPose();
        base.applyTransform(context, poseStack, false);
        poseStack.translate(0.0f, 0.0f, ORIGIN_Z_FLAT);
        poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
        BakedModel originModel = itemRenderer.getModel(original, null, null, 0);
        itemRenderer.render(original, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight, packedOverlay, originModel);
        poseStack.popPose();
    }

    /**
     * ブロック用: GUI のみ。立方体は回転して描かれるので、表面に貼るのではなく画面手前に平面アイコンとして重ねる。
     * ponytail: 手持ちのブロックには出さない（立方体に貼るには面ごとの処理が必要になるため）。
     */
    private static void renderOriginOnBlock(ItemStack original, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, ItemRenderer itemRenderer) {
        if (context != ItemDisplayContext.GUI) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.0f, 0.0f, ORIGIN_Z_BLOCK);
        poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
        itemRenderer.renderStatic(original, context, packedLight, packedOverlay, poseStack, buffer, null, 0);
        poseStack.popPose();
    }
}
