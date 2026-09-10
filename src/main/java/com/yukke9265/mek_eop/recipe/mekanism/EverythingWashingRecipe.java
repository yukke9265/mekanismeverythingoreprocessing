package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicWashingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.fluids.FluidStack;

/**
 * Chemical Washer 用。
 * <p>
 * 入力は「全てのなんでもダーティスラリー」タグで受け、出力は入力に対応するクリーンスラリーにする。
 */
public class EverythingWashingRecipe extends BasicWashingRecipe {

    public EverythingWashingRecipe(FluidStackIngredient fluidInput, ChemicalStackIngredient chemicalInput, ChemicalStack output) {
        super(fluidInput, chemicalInput, output);
    }

    @Override
    public ChemicalStack getOutput(FluidStack fluidStack, ChemicalStack chemicalStack) {
        ChemicalStack base = super.getOutput(fluidStack, chemicalStack);
        Chemical clean = EverythingSlurries.cleanForDirty(chemicalStack.getChemical());
        return new ChemicalStack(EverythingSlurries.holderOf(clean), base.getAmount());
    }

    @Override
    public RecipeSerializer<BasicWashingRecipe> getSerializer() {
        return ModRecipeSerializers.WASHING.get();
    }
}
