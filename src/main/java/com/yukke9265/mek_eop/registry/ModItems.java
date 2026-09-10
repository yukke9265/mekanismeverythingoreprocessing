package com.yukke9265.mek_eop.registry;

import java.util.List;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.item.EverythingBlockItem;
import com.yukke9265.mek_eop.item.EverythingItem;

import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * アイテムの登録。
 * <p>
 * Mekanism の鉱石処理チェーンに合わせて、原石 → ダスト / ダーティダスト / クランプ / シャード / 結晶 → インゴット → ブロック を揃える。
 */
public final class ModItems {
    private ModItems() {
    }

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MekanismEverythingOreProcessing.MODID);

    public static final DeferredItem<EverythingItem> EVERYTHING_RAW_ORE = registerEverything("everything_raw_ore");
    public static final DeferredItem<EverythingItem> EVERYTHING_DUST = registerEverything("everything_dust");
    public static final DeferredItem<EverythingItem> EVERYTHING_DIRTY_DUST = registerEverything("everything_dirty_dust");
    public static final DeferredItem<EverythingItem> EVERYTHING_CLUMP = registerEverything("everything_clump");
    public static final DeferredItem<EverythingItem> EVERYTHING_SHARD = registerEverything("everything_shard");
    public static final DeferredItem<EverythingItem> EVERYTHING_CRYSTAL = registerEverything("everything_crystal");
    public static final DeferredItem<EverythingItem> EVERYTHING_INGOT = registerEverything("everything_ingot");

    public static final DeferredItem<EverythingBlockItem> EVERYTHING_BLOCK = ITEMS.register("everything_block",
            () -> new EverythingBlockItem(ModBlocks.EVERYTHING_BLOCK.get(), new Item.Properties()));

    /** クリエイティブタブ・モデル生成などで一括処理したいときに使う一覧（ブロックアイテム含む）。 */
    public static List<DeferredItem<? extends Item>> allEverythingItems() {
        return List.of(EVERYTHING_RAW_ORE, EVERYTHING_DUST, EVERYTHING_DIRTY_DUST, EVERYTHING_CLUMP, EVERYTHING_SHARD,
                EVERYTHING_CRYSTAL, EVERYTHING_INGOT, EVERYTHING_BLOCK);
    }

    /** 見た目本体のモデル名（登録名のモデルは描画を独自レンダラーに任せるため、実体は別名で持つ）。 */
    public static String baseModelPath(String itemName) {
        return itemName + "_base";
    }

    private static DeferredItem<EverythingItem> registerEverything(String name) {
        return ITEMS.register(name, () -> new EverythingItem(new Item.Properties()));
    }

    public static void register(IEventBus modEventBus) {
        ITEMS.register(modEventBus);
    }
}
