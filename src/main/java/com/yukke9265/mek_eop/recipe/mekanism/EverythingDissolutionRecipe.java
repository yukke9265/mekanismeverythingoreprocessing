package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Chemical Dissolution Chamber 用。
 * <p>
 * JSON に書かれた出力（汎用ダーティスラリー）の量だけを使い、種類は入力アイテムの元情報から決める。
 * 入れ子（深さ 2 以上）はスラリーに残せないので {@link #test} で拒否する。
 */
public class EverythingDissolutionRecipe extends BasicChemicalDissolutionRecipe {

    public EverythingDissolutionRecipe(ItemStackIngredient itemInput, ChemicalStackIngredient chemicalInput, ChemicalStack output, boolean perTickUsage) {
        super(itemInput, chemicalInput, output, perTickUsage);
    }

    @Override
    public boolean test(ItemStack itemStack, ChemicalStack chemicalStack) {
        // 入れ子の元情報はスラリーに載らないので、深さ 2 以上は溶かさない
        return OriginHelper.canDissolve(itemStack) && super.test(itemStack, chemicalStack);
    }

    @Override
    public ChemicalStack getOutput(ItemStack inputItem, ChemicalStack inputChemical) {
        ChemicalStack base = super.getOutput(inputItem, inputChemical);
        OriginalItem origin = OriginHelper.getOrigin(inputItem);
        if (origin == null) {
            return base;
        }
        Chemical slurry = EverythingSlurries.dirtyFor(origin.original().getItem());
        return new ChemicalStack(EverythingSlurries.holderOf(slurry), base.getAmount());
    }

    @Override
    public RecipeSerializer<BasicChemicalDissolutionRecipe> getSerializer() {
        return ModRecipeSerializers.DISSOLUTION.get();
    }
}
