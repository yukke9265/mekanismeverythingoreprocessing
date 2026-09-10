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
        addItemWithNamed(ModItems.EVERYTHING_RAW_ORE, "なんでも原石");
        addItemWithNamed(ModItems.EVERYTHING_DUST, "なんでもダスト");
        addItemWithNamed(ModItems.EVERYTHING_DIRTY_DUST, "なんでもダーティダスト");
        addItemWithNamed(ModItems.EVERYTHING_CLUMP, "なんでもクランプ");
        addItemWithNamed(ModItems.EVERYTHING_SHARD, "なんでもシャード");
        addItemWithNamed(ModItems.EVERYTHING_CRYSTAL, "なんでも結晶");
        addItemWithNamed(ModItems.EVERYTHING_INGOT, "なんでもインゴット");

        addBlock(ModBlocks.EVERYTHING_BLOCK, "なんでもブロック");
        add(ModBlocks.EVERYTHING_BLOCK.get().getDescriptionId() + ".named", "なんでもブロック（%s）");

        add("itemGroup." + MODID, "なんでも鉱石処理");

        add("tooltip." + MODID + ".origin", "元: %s");
        add("tooltip." + MODID + ".no_origin", "元: なし（同じアイテム8個で囲むと刻印されます）");
        add("tooltip." + MODID + ".nesting", "入れ子の深さ: %s");
        add("tooltip." + MODID + ".power", "パワー: %s");

        add("chemical." + MODID + ".dirty_everything_slurry", "なんでもダーティスラリー（%s）");
        add("chemical." + MODID + ".dirty_everything_slurry.unknown", "なんでもダーティスラリー（不明）");
        add("chemical." + MODID + ".clean_everything_slurry", "なんでもクリーンスラリー（%s）");
        add("chemical." + MODID + ".clean_everything_slurry.unknown", "なんでもクリーンスラリー（不明）");

        add(MODID + ".configuration.slurryNamespaceWhitelist", "スラリー生成対象の namespace");
        add(MODID + ".configuration.originStorageMode", "元アイテムの保存形式");
        add(MODID + ".configuration.allowNesting", "入れ子を許可");
        add(MODID + ".configuration.maxNestingDepth", "入れ子の最大深さ");
        add(MODID + ".configuration.blacklist", "変換禁止アイテム");
    }

    private void addItemWithNamed(DeferredItem<? extends Item> item, String name) {
        addItem(item, name);
        add(item.get().getDescriptionId() + ".named", name + "（%s）");
    }
}
