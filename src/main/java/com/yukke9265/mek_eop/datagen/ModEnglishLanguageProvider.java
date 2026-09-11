package com.yukke9265.mek_eop.datagen;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.registries.DeferredItem;

/** en_us。表示名は「Everything ○○」、元情報付きは「Everything ○○ (元アイテム)」。 */
public class ModEnglishLanguageProvider extends LanguageProvider {

    private static final String MODID = MekanismEverythingOreProcessing.MODID;

    public ModEnglishLanguageProvider(PackOutput output) {
        super(output, MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        // 元情報付きの名前は「"元アイテム名" Raw Ore」形式（%s に元アイテム名が入る）
        addItemWithNamed(ModItems.EVERYTHING_RAW_ORE, "Everything Raw Ore", "\"%s\" Raw Ore");
        addItemWithNamed(ModItems.EVERYTHING_DUST, "Everything Dust", "\"%s\" Dust");
        addItemWithNamed(ModItems.EVERYTHING_DIRTY_DUST, "Dirty Everything Dust", "Dirty \"%s\" Dust");
        addItemWithNamed(ModItems.EVERYTHING_CLUMP, "Everything Clump", "\"%s\" Clump");
        addItemWithNamed(ModItems.EVERYTHING_SHARD, "Everything Shard", "\"%s\" Shard");
        addItemWithNamed(ModItems.EVERYTHING_CRYSTAL, "Everything Crystal", "\"%s\" Crystal");
        addItemWithNamed(ModItems.EVERYTHING_INGOT, "Everything Ingot", "\"%s\" Ingot");

        addBlock(ModBlocks.EVERYTHING_BLOCK, "Everything Block");
        add(ModBlocks.EVERYTHING_BLOCK.get().getDescriptionId() + ".named", "\"%s\" Block");

        add("itemGroup." + MODID, "Everything Ore Processing");

        add("tooltip." + MODID + ".origin", "Origin: %s");
        add("tooltip." + MODID + ".no_origin", "Origin: none (surround with 8 identical items to imprint)");
        add("tooltip." + MODID + ".nesting", "Nesting depth: %s");
        add("tooltip." + MODID + ".power", "Power: %s");

        add("jei." + MODID + ".special_crafting", "Everything Special Crafting");
        add("jei." + MODID + ".convert.info", "Center: blank raw ore\nRing: 8 same items");
        add("jei." + MODID + ".restore.info", "One Everything Ingot\nwith origin");

        // アイテム名と同じ「"元アイテム" ～」形式
        add("chemical." + MODID + ".dirty_everything_slurry", "\"%s\" Dirty Slurry");
        add("chemical." + MODID + ".dirty_everything_slurry.unknown", "\"Unknown\" Dirty Slurry");
        add("chemical." + MODID + ".clean_everything_slurry", "\"%s\" Clean Slurry");
        add("chemical." + MODID + ".clean_everything_slurry.unknown", "\"Unknown\" Clean Slurry");

        // Config 画面
        add(MODID + ".configuration.slurryNamespaceWhitelist", "Slurry namespaces (empty = almost all; heavy on big packs)");
        add(MODID + ".configuration.originStorageMode", "Origin storage mode (ITEM_ID disables nesting)");
        add(MODID + ".configuration.allowNesting", "Allow nesting (ignored under ITEM_ID; dissolution still rejects nests)");
        add(MODID + ".configuration.maxNestingDepth", "Max nesting depth");
        add(MODID + ".configuration.blacklist", "Conversion blacklist");
    }

    private void addItemWithNamed(DeferredItem<? extends Item> item, String name, String namedFormat) {
        addItem(item, name);
        add(item.get().getDescriptionId() + ".named", namedFormat);
    }
}
