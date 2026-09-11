package com.yukke9265.mek_eop.jei;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;

import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

/**
 * JEI 用の説明レシピ（実際の CustomRecipe とは別物。見た目の見本だけ）。
 * @param inputs 長さ 9 の 3x3 グリッド（空スロットは EMPTY）
 */
public record SpecialCraftingJeiRecipe(
        ResourceLocation id,
        ItemStack[] inputs,
        ItemStack output,
        String infoKey
) {
    public static final RecipeType<SpecialCraftingJeiRecipe> TYPE =
            RecipeType.create(MekanismEverythingOreProcessing.MODID, "special_crafting", SpecialCraftingJeiRecipe.class);

    public SpecialCraftingJeiRecipe {
        if (inputs.length != 9) {
            throw new IllegalArgumentException("inputs must be length 9");
        }
    }
}
