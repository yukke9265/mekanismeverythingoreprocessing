package com.yukke9265.mek_eop.recipe.crafting;

import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.HolderLookup;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 復元レシピ: なんでもインゴット ×1（単独） → 元アイテム ×1。
 * <p>
 * 元情報を持たないインゴット（スラリー経由で対応が取れなかったもの等）は復元できない。
 */
public class RestoreRecipe extends CustomRecipe {

    public RestoreRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        ItemStack single = findSingleStack(input);
        return single != null && single.is(ModItems.EVERYTHING_INGOT.get()) && OriginHelper.hasOrigin(single);
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack single = findSingleStack(input);
        if (single == null) {
            return ItemStack.EMPTY;
        }
        OriginalItem origin = OriginHelper.getOrigin(single);
        if (origin == null) {
            return ItemStack.EMPTY;
        }
        return origin.restore();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 1;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RESTORE.get();
    }

    /** グリッド内に非空スタックがちょうど 1 つならそれを返す。それ以外は null。 */
    static ItemStack findSingleStack(CraftingInput input) {
        ItemStack found = null;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (found != null) {
                return null;
            }
            found = stack;
        }
        return found;
    }
}
