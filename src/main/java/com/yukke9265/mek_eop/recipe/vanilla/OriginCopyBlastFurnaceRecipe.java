package com.yukke9265.mek_eop.recipe.vanilla;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;

/** バニラ溶鉱炉用。出力に入力の元情報をコピーする。 */
public class OriginCopyBlastFurnaceRecipe extends BlastingRecipe {

    public OriginCopyBlastFurnaceRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
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
        return ModRecipeSerializers.VANILLA_BLASTING.get();
    }
}
