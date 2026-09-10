package com.yukke9265.mek_eop.datagen;

import net.neoforged.neoforge.data.event.GatherDataEvent;

/** datagen の入口。runData 実行時に各 provider を登録する。 */
public final class ModDatagen {
    private ModDatagen() {
    }

    public static void gatherData(GatherDataEvent event) {
        if (event.includeClient()) {
            event.createProvider(output -> new ModBlockStateProvider(output, event.getExistingFileHelper()));
            event.createProvider(output -> new ModItemModelProvider(output, event.getExistingFileHelper()));
            event.createProvider(ModEnglishLanguageProvider::new);
            event.createProvider(ModJapaneseLanguageProvider::new);
        }
        if (event.includeServer()) {
            event.createProvider(ModRecipeProvider::new);
            event.createProvider(ModBlockLootTableProvider::new);
            var blockTags = event.createProvider(
                    (output, lookup) -> new ModBlockTagProvider(output, lookup, event.getExistingFileHelper()));
            event.createProvider(
                    (output, lookup) -> new ModItemTagProvider(output, lookup, blockTags.contentsGetter(), event.getExistingFileHelper()));
        }
    }
}
