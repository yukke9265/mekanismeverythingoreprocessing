package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.recipes.basic.BasicEnrichingRecipe;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/** Enrichment Chamber 用。出力に入力の元情報をコピーする。 */
public class OriginCopyEnrichingRecipe extends BasicEnrichingRecipe {

    public OriginCopyEnrichingRecipe(ItemStackIngredient input, ItemStack output) {
        super(input, output);
    }

    @Override
    public ItemStack getOutput(ItemStack input) {
        return OriginHelper.copyOrigin(input, super.getOutput(input));
    }

    @Override
    public RecipeSerializer<BasicEnrichingRecipe> getSerializer() {
        return ModRecipeSerializers.ENRICHING.get();
    }
}
