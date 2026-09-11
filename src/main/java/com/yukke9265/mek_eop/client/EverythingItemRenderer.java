package com.yukke9265.mek_eop.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.util.OriginDisplay;
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
 * 1. 見た目本体（平面は {@code everything_x_base}、ブロックは {@code block/everything_block}）を通常描画する。
 * 2. 元アイテムがあれば重ねる。平面は中央、ブロックは 6 面（世界の BER と同じ）。
 * <p>
 * 登録名のモデルは builtin/entity なので、ItemRenderer はこのクラスに描画を委ねてくる。
 */
public class EverythingItemRenderer extends BlockEntityWithoutLevelRenderer {

    /** 平面アイテムの元アイコンの大きさ（本体に対する比率）。 */
    private static final float ORIGIN_SCALE = 0.7f;
    /** 平面アイテム: 本体（厚み 1/16）の手前に出すための法線方向オフセット。 */
    private static final float ORIGIN_Z_FLAT = 0.1f;

    public EverythingItemRenderer() {
        super(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
    }

    /** 平面アイテムの見た目本体モデル ID。 */
    public static ModelResourceLocation baseModel(ItemStack stack) {
        String name = BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
        return ModelResourceLocation.standalone(MekanismEverythingOreProcessing.rl("item/" + ModItems.baseModelPath(name)));
    }

    /** なんでもブロックの見た目本体（ブロックモデルそのもの）。 */
    public static ModelResourceLocation blockBaseModel() {
        return ModelResourceLocation.standalone(MekanismEverythingOreProcessing.rl("block/everything_block"));
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer, int packedLight, int packedOverlay) {
        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();

        // ここに来た時点で座標系は 0..1（ItemRenderer が -0.5 ずらした後）。中心を原点に戻す
        poseStack.pushPose();
        poseStack.translate(0.5f, 0.5f, 0.5f);

        if (stack.getItem() instanceof BlockItem) {
            renderBlockItem(stack, context, poseStack, buffer, packedLight, packedOverlay, itemRenderer, minecraft);
        } else {
            renderFlatItem(stack, context, poseStack, buffer, packedLight, packedOverlay, itemRenderer, minecraft);
        }
        poseStack.popPose();
    }

    private static void renderFlatItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, ItemRenderer itemRenderer, Minecraft minecraft) {
        BakedModel base = minecraft.getModelManager().getModel(baseModel(stack));
        itemRenderer.render(stack, context, false, poseStack, buffer, packedLight, packedOverlay, base);

        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            return;
        }
        ItemStack icon = OriginDisplay.iconStack(origin.original());
        if (icon.isEmpty()) {
            return;
        }
        // 本体と同じ display 変換を先に掛けてから、その座標系で法線方向に少しずらして描く
        poseStack.pushPose();
        base.applyTransform(context, poseStack, false);
        poseStack.translate(0.0f, 0.0f, ORIGIN_Z_FLAT);
        poseStack.scale(ORIGIN_SCALE, ORIGIN_SCALE, ORIGIN_SCALE);
        BakedModel originModel = itemRenderer.getModel(icon, null, null, 0);
        itemRenderer.render(icon, ItemDisplayContext.NONE, false, poseStack, buffer, packedLight, packedOverlay, originModel);
        poseStack.popPose();
    }

    private static void renderBlockItem(ItemStack stack, ItemDisplayContext context, PoseStack poseStack, MultiBufferSource buffer,
            int packedLight, int packedOverlay, ItemRenderer itemRenderer, Minecraft minecraft) {
        BakedModel blockModel = minecraft.getModelManager().getModel(blockBaseModel());
        // 立方体本体（ティントは ItemColors 経由）
        itemRenderer.render(stack, context, false, poseStack, buffer, packedLight, packedOverlay, blockModel);

        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            return;
        }
        ItemStack icon = OriginDisplay.iconStack(origin.original());
        if (icon.isEmpty()) {
            return;
        }
        // ItemRenderer.render と同じ display 変換 + 角原点へのずらしを掛けてから、世界と同じ 6 面貼りを使う
        poseStack.pushPose();
        blockModel.applyTransform(context, poseStack, false);
        poseStack.translate(-0.5f, -0.5f, -0.5f);
        EverythingBlockRenderer.renderOriginOnFaces(icon, poseStack, buffer, packedLight, packedOverlay, null);
        poseStack.popPose();
    }
}
