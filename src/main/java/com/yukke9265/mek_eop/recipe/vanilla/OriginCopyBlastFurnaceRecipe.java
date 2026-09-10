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

/** 繝舌ル繝ｩ貅ｶ驩ｱ轤臥畑縲ょ・蜉帙↓蜈･蜉帙・蜈・ュ蝣ｱ繧偵さ繝斐・縺吶ｋ縲・*/
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
