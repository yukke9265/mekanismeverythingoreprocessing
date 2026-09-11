package com.yukke9265.mek_eop.recipe.vanilla;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * バニラかまど用の精錬レシピ。出力に入力の元情報をコピーする。
 * <p>
 * isSpecial を true にして、Mekanism がバニラ精錬を Energized Smelter へ取り込む処理（Component が消える）から除外する。
 * Energized Smelter 側は mekanism:smelting の専用レシピで対応する。
 */
public class OriginCopyFurnaceRecipe extends SmeltingRecipe {

    public OriginCopyFurnaceRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
        super(group, category, ingredient, result, experience, cookingTime);
    }

    @Override
    public ItemStack assemble(SingleRecipeInput input, HolderLookup.Provider registries) {
        return OriginHelper.copyOrigin(input.item(), super.assemble(input, registries));
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.VANILLA_SMELTING.get();
    }
}
