package com.yukke9265.mek_eop.recipe.vanilla;

import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.SmeltingRecipe;

/**
 * 繝舌ル繝ｩ縺九∪縺ｩ逕ｨ縺ｮ邊ｾ骭ｬ繝ｬ繧ｷ繝斐ょ・蜉帙↓蜈･蜉帙・蜈・ュ蝣ｱ繧偵さ繝斐・縺吶ｋ縲・
 * <p>
 * isSpecial 繧・true 縺ｫ縺励※縲｀ekanism 縺後ヰ繝九Λ邊ｾ骭ｬ繧・Energized Smelter 縺ｸ蜿悶ｊ霎ｼ繧蜃ｦ逅・ｼ・omponent 縺梧ｶ医∴繧具ｼ峨°繧蛾勁螟悶☆繧九・
 * Energized Smelter 蛛ｴ縺ｯ mekanism:smelting 縺ｮ蟆ら畑繝ｬ繧ｷ繝斐〒蟇ｾ蠢懊☆繧九・
 */
public class OriginCopyFurnaceRecipe extends SmeltingRecipe {

    public OriginCopyFurnaceRecipe(String group, CookingBookCategory category, Ingredient ingredient, ItemStack result, float experience, int cookingTime) {
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
        return ModRecipeSerializers.VANILLA_SMELTING.get();
    }
}
