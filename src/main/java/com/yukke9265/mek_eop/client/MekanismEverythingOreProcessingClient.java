package com.yukke9265.mek_eop.client;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.block.EverythingBlockEntity;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModBlockEntities;
import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * クライアント専用の初期化。
 * <p>
 * なんでも○○の見た目 = 「灰色ベース × power 由来のティント」+「ティント無しオーバーレイ」+「右下に元アイテムの小アイコン」。
 */
@Mod(value = MekanismEverythingOreProcessing.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MekanismEverythingOreProcessing.MODID, value = Dist.CLIENT)
public class MekanismEverythingOreProcessingClient {

    /** ティント無し（白）。 */
    private static final int NO_TINT = 0xFFFFFFFF;

    /** レンダラーは描画スレッドで初めて必要になったときに 1 つだけ作る。 */
    private static EverythingItemRenderer itemRenderer;

    public MekanismEverythingOreProcessingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    /** 見た目本体のモデルは登録名と紐付かないので、追加モデルとして読み込ませる。 */
    @SubscribeEvent
    static void onRegisterAdditionalModels(ModelEvent.RegisterAdditional event) {
        for (DeferredItem<? extends Item> item : ModItems.allEverythingItems()) {
            if (item == ModItems.EVERYTHING_BLOCK) {
                continue;
            }
            event.register(EverythingItemRenderer.baseModel(item.get().getDefaultInstance()));
        }
        // ブロックの見た目本体（BEWLR から参照する）
        event.register(EverythingItemRenderer.blockBaseModel());
    }

    /** 置いたブロックの各面に元アイテムアイコンを重ねる。 */
    @SubscribeEvent
    static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(ModBlockEntities.EVERYTHING_BLOCK.get(), EverythingBlockRenderer::new);
    }

    /** 登録名のモデル（builtin/entity）の描画先として独自レンダラーを結び付ける。 */
    @SubscribeEvent
    static void onRegisterClientExtensions(RegisterClientExtensionsEvent event) {
        IClientItemExtensions extensions = new IClientItemExtensions() {
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (itemRenderer == null) {
                    itemRenderer = new EverythingItemRenderer();
                }
                return itemRenderer;
            }
        };
        for (DeferredItem<? extends Item> item : ModItems.allEverythingItems()) {
            event.registerItem(extensions, item.get());
        }
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        for (DeferredItem<? extends Item> item : ModItems.allEverythingItems()) {
            event.register((stack, tintIndex) -> {
                // layer0 だけ色を付ける。layer1（オーバーレイ）はそのまま
                if (tintIndex != 0) {
                    return NO_TINT;
                }
                OriginalItem origin = OriginHelper.getOrigin(stack);
                if (origin == null) {
                    return NO_TINT;
                }
                return 0xFF000000 | OriginHelper.tintColor(origin.power());
            }, item.get());
        }
    }

    @SubscribeEvent
    static void onRegisterBlockColors(RegisterColorHandlersEvent.Block event) {
        event.register((state, level, pos, tintIndex) -> {
            if (level == null || pos == null) {
                return NO_TINT;
            }
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if (!(blockEntity instanceof EverythingBlockEntity everything) || everything.getOrigin() == null) {
                return NO_TINT;
            }
            return 0xFF000000 | OriginHelper.tintColor(everything.getOrigin().power());
        }, ModBlocks.EVERYTHING_BLOCK.get());
    }
}
