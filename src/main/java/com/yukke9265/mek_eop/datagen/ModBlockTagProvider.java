package com.yukke9265.mek_eop.datagen;

import java.util.concurrent.CompletableFuture;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModBlocks;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** ブロックタグ。なんでもブロックはツルハシで採掘可能にする。 */
public class ModBlockTagProvider extends BlockTagsProvider {

    public ModBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, MekanismEverythingOreProcessing.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(BlockTags.MINEABLE_WITH_PICKAXE).add(ModBlocks.EVERYTHING_BLOCK.get());
        tag(BlockTags.NEEDS_STONE_TOOL).add(ModBlocks.EVERYTHING_BLOCK.get());
    }
}
