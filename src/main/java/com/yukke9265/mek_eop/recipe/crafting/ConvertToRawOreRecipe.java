package com.yukke9265.mek_eop.recipe.crafting;

import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
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
 * 変換レシピ: 中央に「元情報なしのなんでも原石」、周囲 8 マスに同じ任意アイテム → その元情報を持つなんでも原石 ×8。
 * <p>
 * 周囲のアイテム 1 個がなんでも原石 1 個になる（1:1）。中央の原石は消費される触媒。
 * 「任意アイテム」は通常の Ingredient で表せないので CustomRecipe として実装する。
 * 周囲のアイテムは Config のブラックリスト・入れ子制限を通ったものだけ受け付ける。
 */
public class ConvertToRawOreRecipe extends CustomRecipe {

    private static final int CENTER_INDEX = 4;
    private static final int OUTPUT_COUNT = 8;

    public ConvertToRawOreRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        if (input.width() != 3 || input.height() != 3) {
            return false;
        }
        ItemStack center = input.getItem(CENTER_INDEX);
        if (!center.is(ModItems.EVERYTHING_RAW_ORE.get()) || OriginHelper.hasOrigin(center)) {
            return false;
        }
        ItemStack source = findSource(input);
        if (source == null || !OriginHelper.canConvert(source)) {
            return false;
        }
        for (int i = 0; i < 9; i++) {
            if (i == CENTER_INDEX) {
                continue;
            }
            // 周囲は全て同じアイテム（Component まで同じ）でなければならない
            if (!ItemStack.isSameItemSameComponents(input.getItem(i), source)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack source = findSource(input);
        if (source == null) {
            return ItemStack.EMPTY;
        }
        return OriginHelper.withOriginFrom(new ItemStack(ModItems.EVERYTHING_RAW_ORE.get(), OUTPUT_COUNT), source);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput input) {
        // 周囲のアイテムは丸ごと「原石」になるので、バケツ等の容器も残さない
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

    /** 周囲の代表アイテム（左上）。空なら null。 */
    private static ItemStack findSource(CraftingInput input) {
        ItemStack first = input.getItem(0);
        return first.isEmpty() ? null : first;
    }
}
