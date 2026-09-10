package com.yukke9265.mek_eop.datagen;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModItems;

import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** item model の生成。通常アイテムは item/generated、ブロックアイテムはブロックモデルを親にする。 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MekanismEverythingOreProcessing.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItems.EVERYTHING_RAW_ORE.get());
        basicItem(ModItems.EVERYTHING_DUST.get());
        basicItem(ModItems.EVERYTHING_DIRTY_DUST.get());
        basicItem(ModItems.EVERYTHING_CLUMP.get());
        basicItem(ModItems.EVERYTHING_SHARD.get());
        basicItem(ModItems.EVERYTHING_CRYSTAL.get());
        basicItem(ModItems.EVERYTHING_INGOT.get());
        basicItem(ModItems.EVERYTHING_NUGGET.get());
        withExistingParent("everything_block", modLoc("block/everything_block"));
    }
}
