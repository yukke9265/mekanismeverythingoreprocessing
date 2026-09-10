package com.yukke9265.mek_eop.client;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.block.EverythingBlockEntity;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * クライアント専用の初期化。
 * <p>
 * なんでも○○の色は「灰色のベーステクスチャ × power から作ったティント色」で表現する。
 */
@Mod(value = MekanismEverythingOreProcessing.MODID, dist = Dist.CLIENT)
@EventBusSubscriber(modid = MekanismEverythingOreProcessing.MODID, value = Dist.CLIENT)
public class MekanismEverythingOreProcessingClient {

    /** ティント無し（白）。 */
    private static final int NO_TINT = 0xFFFFFFFF;

    public MekanismEverythingOreProcessingClient(ModContainer container) {
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        for (DeferredItem<? extends Item> item : ModItems.allEverythingItems()) {
            event.register((stack, tintIndex) -> {
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
