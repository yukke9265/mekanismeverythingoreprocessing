package com.yukke9265.mek_eop.recipe.mekanism;

import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.basic.BasicChemicalCrystallizerRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Chemical Crystallizer 用。
 * <p>
 * 入力は「全てのなんでもクリーンスラリー」タグで受け、スラリーから元アイテムを逆引きして
 * 元情報付きのなんでも結晶を出す。逆引きできない（汎用スラリー）場合は元情報なしの結晶になる。
 */
public class EverythingCrystallizingRecipe extends BasicChemicalCrystallizerRecipe {

    public EverythingCrystallizingRecipe(ChemicalStackIngredient input, ItemStack output) {
        super(input, output);
    }

    @Override
    public ItemStack getOutput(ChemicalStack input) {
        ItemStack result = super.getOutput(input);
        Item source = EverythingSlurries.sourceItemOf(input.getChemical());
        if (source != null) {
            // スラリーはアイテム種類しか覚えていないので、Component 無しの素のアイテムから元情報を作る
            OriginHelper.withOriginFrom(result, new ItemStack(source));
        }
        return result;
    }

    @Override
    public RecipeSerializer<BasicChemicalCrystallizerRecipe> getSerializer() {
        return ModRecipeSerializers.CRYSTALLIZING.get();
    }
}
