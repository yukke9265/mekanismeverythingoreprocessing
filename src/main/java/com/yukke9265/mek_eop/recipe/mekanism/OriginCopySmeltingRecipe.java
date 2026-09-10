package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.recipes.basic.BasicSmeltingRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Energized Smelter 用。出力に入力の元情報をコピーする。 */
public class OriginCopySmeltingRecipe extends BasicSmeltingRecipe {

    public OriginCopySmeltingRecipe(ItemStackIngredient input, ItemStack output) {
        super(input, output);
    }

    @Override
    public ItemStack getOutput(ItemStack input) {
        return OriginHelper.copyOrigin(input, super.getOutput(input));
    }

    @Override
    public RecipeSerializer<BasicSmeltingRecipe> getSerializer() {
        return ModRecipeSerializers.SMELTING.get();
    }
}
