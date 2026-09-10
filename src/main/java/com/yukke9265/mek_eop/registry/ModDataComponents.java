package com.yukke9265.mek_eop.registry;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.component.OriginalItem;

import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** Data Component の登録。 */
public final class ModDataComponents {
    private ModDataComponents() {
    }

    public static final DeferredRegister.DataComponents DATA_COMPONENTS =
            DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MekanismEverythingOreProcessing.MODID);

    /** 元アイテムの情報。なんでも○○系アイテムが全て持つ。 */
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<OriginalItem>> ORIGINAL_ITEM =
            DATA_COMPONENTS.registerComponentType("original_item", builder -> builder
                    .persistent(OriginalItem.CODEC)
                    .networkSynchronized(OriginalItem.STREAM_CODEC));

    public static void register(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
