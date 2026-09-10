package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicPurifyingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Purification Chamber 用。出力に入力の元情報をコピーする。 */
public class OriginCopyPurifyingRecipe extends BasicPurifyingRecipe {

    public OriginCopyPurifyingRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack output, boolean perTickUsage) {
        super(itemInput, chemicalInput, output, perTickUsage);
    }

    @Override
    public ItemStack getOutput(ItemStack inputItem, ChemicalStack inputChemical) {
        return OriginHelper.copyOrigin(inputItem, super.getOutput(inputItem, inputChemical));
    }

    @Override
    public RecipeSerializer<BasicPurifyingRecipe> getSerializer() {
        return ModRecipeSerializers.PURIFYING.get();
    }
}
