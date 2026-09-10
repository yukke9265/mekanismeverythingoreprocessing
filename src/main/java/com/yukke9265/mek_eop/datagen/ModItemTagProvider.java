package com.yukke9265.mek_eop.datagen;

import java.util.concurrent.CompletableFuture;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModTags;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/**
 * アイテムタグ。
 * <p>
 * なんでも○○は c:ingots などの共通タグには入れない。
 * 他 mod のレシピに拾われて元情報が消えるのを防ぐため。
 */
public class ModItemTagProvider extends ItemTagsProvider {

    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagsProvider.TagLookup<Block>> blockTags, ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, blockTags, MekanismEverythingOreProcessing.MODID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // 空のまま作っておき、データパック側で追加できるようにする
        tag(ModTags.Items.BLACKLIST);
    }
}
