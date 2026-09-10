package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.recipes.basic.BasicCrushingRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Crusher 用。出力に入力の元情報をコピーする。 */
public class OriginCopyCrushingRecipe extends BasicCrushingRecipe {

    public OriginCopyCrushingRecipe(ItemStackIngredient input, ItemStack output) {
        super(input, output);
    }

    @Override
    public ItemStack getOutput(ItemStack input) {
        return OriginHelper.copyOrigin(input, super.getOutput(input));
    }

    @Override
    public RecipeSerializer<BasicCrushingRecipe> getSerializer() {
        return ModRecipeSerializers.CRUSHING.get();
    }
}
