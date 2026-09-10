package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicInjectingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Chemical Injection Chamber 用。出力に入力の元情報をコピーする。 */
public class OriginCopyInjectingRecipe extends BasicInjectingRecipe {

    public OriginCopyInjectingRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ItemStack output, boolean perTickUsage) {
        super(itemInput, chemicalInput, output, perTickUsage);
    }

    @Override
    public ItemStack getOutput(ItemStack inputItem, ChemicalStack inputChemical) {
        return OriginHelper.copyOrigin(inputItem, super.getOutput(inputItem, inputChemical));
    }

    @Override
    public RecipeSerializer<BasicInjectingRecipe> getSerializer() {
        return ModRecipeSerializers.INJECTING.get();
    }
}
