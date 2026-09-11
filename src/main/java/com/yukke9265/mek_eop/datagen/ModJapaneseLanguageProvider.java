package com.yukke9265.mek_eop.datagen;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredItem;

/** ja_jp。表示名は「なんでも○○」、元情報付きは「なんでも○○（元アイテム）」。 */
public class ModJapaneseLanguageProvider extends LanguageProvider {

    private static final String MODID = MekanismEverythingOreProcessing.MODID;

    public ModJapaneseLanguageProvider(PackOutput output) {
        super(output, MODID, "ja_jp");
    }

    @Override
    protected void addTranslations() {
        // 元情報付きの名前は「"元アイテム名" 原石」形式（%s に元アイテム名が入る）
        addItemWithNamed(ModItems.EVERYTHING_RAW_ORE, "なんでも原石", "\"%s\" 原石");
        addItemWithNamed(ModItems.EVERYTHING_DUST, "なんでもダスト", "\"%s\" ダスト");
        addItemWithNamed(ModItems.EVERYTHING_DIRTY_DUST, "なんでもダーティダスト", "\"%s\" ダーティダスト");
        addItemWithNamed(ModItems.EVERYTHING_CLUMP, "なんでもクランプ", "\"%s\" クランプ");
        addItemWithNamed(ModItems.EVERYTHING_SHARD, "なんでもシャード", "\"%s\" シャード");
        addItemWithNamed(ModItems.EVERYTHING_CRYSTAL, "なんでも結晶", "\"%s\" 結晶");
        addItemWithNamed(ModItems.EVERYTHING_INGOT, "なんでもインゴット", "\"%s\" インゴット");

        addBlock(ModBlocks.EVERYTHING_BLOCK, "なんでもブロック");
        add(ModBlocks.EVERYTHING_BLOCK.get().getDescriptionId() + ".named", "\"%s\" ブロック");

        add("itemGroup." + MODID, "なんでも鉱石処理");

        add("tooltip." + MODID + ".origin", "元: %s");
        add("tooltip." + MODID + ".no_origin", "元: なし（同じアイテム8個で囲むと刻印されます）");
        add("tooltip." + MODID + ".nesting", "入れ子の深さ: %s");
        add("tooltip." + MODID + ".power", "パワー: %s");

        add("jei." + MODID + ".special_crafting", "なんでも特殊クラフト");
        add("jei." + MODID + ".convert.info", "中央: 空白の原石 / 周囲: 同じアイテム8個");
        add("jei." + MODID + ".restore.info", "元情報付きなんでもインゴットを1個だけ置く");

        // アイテム名と同じ「"元アイテム" ～」形式
        add("chemical." + MODID + ".dirty_everything_slurry", "\"%s\" ダーティスラリー");
        add("chemical." + MODID + ".dirty_everything_slurry.unknown", "\"不明\" ダーティスラリー");
        add("chemical." + MODID + ".clean_everything_slurry", "\"%s\" クリーンスラリー");
        add("chemical." + MODID + ".clean_everything_slurry.unknown", "\"不明\" クリーンスラリー");

        add(MODID + ".configuration.slurryNamespaceWhitelist", "スラリー生成対象の namespace");
        add(MODID + ".configuration.originStorageMode", "元アイテムの保存形式");
        add(MODID + ".configuration.allowNesting", "入れ子を許可");
        add(MODID + ".configuration.maxNestingDepth", "入れ子の最大深さ");
        add(MODID + ".configuration.blacklist", "変換禁止アイテム");
    }

    private void addItemWithNamed(DeferredItem<? extends Item> item, String name, String namedFormat) {
        addItem(item, name);
        add(item.get().getDescriptionId() + ".named", namedFormat);
    }
}
