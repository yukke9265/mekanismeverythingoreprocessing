package com.yukke9265.mek_eop;

import java.util.Optional;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;
import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.datapack.EverythingRuntimePack;
import com.yukke9265.mek_eop.registry.ModBlockEntities;
import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModCreativeTabs;
import com.yukke9265.mek_eop.registry.ModDataComponents;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackSelectionConfig;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackSource;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.event.AddPackFindersEvent;

/**
 * Mekanism Everything Ore Processing のエントリポイント。
 * <p>
 * 流れ: 任意アイテム + オスミウム → なんでも原石 → Mekanism の鉱石処理 → なんでもインゴット → 元アイテムへ復元。
 */
@Mod(MekanismEverythingOreProcessing.MODID)
public class MekanismEverythingOreProcessing {

    public static final String MODID = "mekanismeverythingoreprocessing";
    public static final Logger LOGGER = LogUtils.getLogger();

    public MekanismEverythingOreProcessing(IEventBus modEventBus, ModContainer modContainer) {
        // 登録順: Component → Block → BlockEntity → Item → タブ → レシピシリアライザ
        ModDataComponents.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModItems.register(modEventBus);
        ModCreativeTabs.register(modEventBus);
        ModRecipeSerializers.register(modEventBus);

        // 全アイテム分のスラリーは RegisterEvent の中で動的に登録する
        modEventBus.addListener(EverythingSlurries::onRegister);
        // スラリー用の化学物質タグを実行時データパックとして差し込む
        modEventBus.addListener(this::addRuntimePack);

        // STARTUP: 登録前に必要（スラリー対象の namespace）。COMMON: ゲーム中の挙動
        modContainer.registerConfig(ModConfig.Type.STARTUP, Config.STARTUP_SPEC);
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.COMMON_SPEC);
    }

    private void addRuntimePack(AddPackFindersEvent event) {
        if (event.getPackType() != PackType.SERVER_DATA) {
            return;
        }
        PackLocationInfo location = new PackLocationInfo(EverythingRuntimePack.PACK_ID,
                Component.literal("Everything Slurry Tags"), PackSource.BUILT_IN, Optional.empty());
        Pack.ResourcesSupplier supplier = new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(PackLocationInfo info) {
                return new EverythingRuntimePack(info);
            }

            @Override
            public PackResources openFull(PackLocationInfo info, Pack.Metadata metadata) {
                return new EverythingRuntimePack(info);
            }
        };
        // required=true: 常に有効で、ユーザーが外せないパックにする
        Pack pack = Pack.readMetaAndCreate(location, supplier, PackType.SERVER_DATA,
                new PackSelectionConfig(true, Pack.Position.TOP, false));
        if (pack == null) {
            LOGGER.error("Failed to create the runtime data pack for Everything Slurry tags");
            return;
        }
        event.addRepositorySource(consumer -> consumer.accept(pack));
    }

    public static ResourceLocation rl(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
