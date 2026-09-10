package com.yukke9265.mek_eop.registry;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/** クリエイティブタブ。元情報なしの各アイテムを並べる。 */
public final class ModCreativeTabs {
    private ModCreativeTabs() {
    }

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MekanismEverythingOreProcessing.MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mekanismeverythingoreprocessing"))
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> ModItems.EVERYTHING_RAW_ORE.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                for (DeferredItem<? extends Item> item : ModItems.allEverythingItems()) {
                    output.accept(item.get());
                }
            }).build());

    public static void register(IEventBus modEventBus) {
        CREATIVE_MODE_TABS.register(modEventBus);
    }
}
