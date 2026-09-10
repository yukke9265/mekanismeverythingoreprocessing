package com.yukke9265.mek_eop.recipe.crafting;

import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.registry.ModTags;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 変換レシピ: 中央に任意アイテム、周囲 8 マスにオスミウムインゴット → なんでも原石 ×1。
 * <p>
 * 「任意アイテム」は通常の Ingredient で表せないので CustomRecipe として実装する。
 * 中央のアイテムは Config のブラックリスト・入れ子制限を通ったものだけ受け付ける。
 */
public class ConvertToRawOreRecipe extends CustomRecipe {

    private static final int CENTER_INDEX = 4;

    public ConvertToRawOreRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            ItemStack stack = input.getItem(i);
            if (i == CENTER_INDEX) {
                if (!OriginHelper.canConvert(stack)) {
                    return false;
                }
            } else if (!stack.is(ModTags.Items.CONVERSION_CATALYST)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = input.getItem(CENTER_INDEX);
        return OriginHelper.withOriginFrom(new ItemStack(ModItems.EVERYTHING_RAW_ORE.get()), source);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        // 中央のアイテムは丸ごと「原石」になるので、バケツ等の容器も残さない
        return NonNullList.withSize(input.size(), ItemStack.EMPTY);
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.CONVERT_TO_RAW_ORE.get();
    }
}
