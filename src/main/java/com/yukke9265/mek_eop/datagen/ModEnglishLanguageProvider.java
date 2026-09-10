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
        addItemWithNamed(ModItems.EVERYTHING_RAW_ORE, "Everything Raw Ore");
        addItemWithNamed(ModItems.EVERYTHING_DUST, "Everything Dust");
        addItemWithNamed(ModItems.EVERYTHING_DIRTY_DUST, "Dirty Everything Dust");
        addItemWithNamed(ModItems.EVERYTHING_CLUMP, "Everything Clump");
        addItemWithNamed(ModItems.EVERYTHING_SHARD, "Everything Shard");
        addItemWithNamed(ModItems.EVERYTHING_CRYSTAL, "Everything Crystal");
        addItemWithNamed(ModItems.EVERYTHING_INGOT, "Everything Ingot");

        addBlock(ModBlocks.EVERYTHING_BLOCK, "Everything Block");
        add(ModBlocks.EVERYTHING_BLOCK.get().getDescriptionId() + ".named", "Everything Block (%s)");

        add("itemGroup." + MODID, "Everything Ore Processing");

        add("tooltip." + MODID + ".origin", "Origin: %s");
        add("tooltip." + MODID + ".no_origin", "Origin: unknown (cannot be restored)");
        add("tooltip." + MODID + ".nesting", "Nesting depth: %s");
        add("tooltip." + MODID + ".power", "Power: %s");

        add("chemical." + MODID + ".dirty_everything_slurry", "Dirty Everything Slurry (%s)");
        add("chemical." + MODID + ".dirty_everything_slurry.unknown", "Dirty Everything Slurry (Unknown)");
        add("chemical." + MODID + ".clean_everything_slurry", "Clean Everything Slurry (%s)");
        add("chemical." + MODID + ".clean_everything_slurry.unknown", "Clean Everything Slurry (Unknown)");

        // Config 画面
        add(MODID + ".configuration.slurryNamespaceWhitelist", "Slurry namespace whitelist");
        add(MODID + ".configuration.originStorageMode", "Origin storage mode");
        add(MODID + ".configuration.allowNesting", "Allow nesting");
        add(MODID + ".configuration.maxNestingDepth", "Max nesting depth");
        add(MODID + ".configuration.blacklist", "Conversion blacklist");
    }

    private void addItemWithNamed(DeferredItem<? extends Item> item, String name) {
        addItem(item, name);
        add(item.get().getDescriptionId() + ".named", name + " (%s)");
    }
}
