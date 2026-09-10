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
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

/**
 * なんでも○○の描画。
 * <p>
 * 1. 見た目本体（{@code everything_x_base} モデル。ベース層は power 由来の色でティント）を通常描画する。
 * 2. 元アイテムがあれば、右下に元アイテムのアイコンを半分の大きさで重ねる（GUI と額縁のみ）。
 * <p>
 * 登録名のモデルは builtin/entity なので、ItemRenderer はこのクラスに描画を委ねてくる。
 */
public class EverythingItemRenderer extends BlockEntityWithoutLevelRenderer {

    /** 元アイテムアイコンの大きさ（本体に対する比率）と位置。 */
    private static final float ORIGIN_SCALE = 0.5f;
    private static final float ORIGIN_OFFSET = 0.25f;
    private static final float ORIGIN_Z = 0.3f;

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

        // 2. 元アイテムの小アイコン（GUI と額縁のみ。手持ちでは邪魔になるので出さない）
        OriginalItem origin = OriginHelper.getOrigin(stack);
        boolean showOrigin = context == ItemDisplayContext.GUI || context == ItemDisplayContext.FIXED;
        if (origin != null && showOrigin) {
            // 右下（GUI では +y が上）へずらし、本体より手前に出す
            poseStack.translate(ORIGIN_OFFSET, -ORIGIN_OFFSET, ORIGIN_Z);
            poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
            itemRenderer.renderStatic(origin.original(), context, packedLight, packedOverlay, poseStack, buffer, null, 0);
        }
        poseStack.popPose();
    }
}
