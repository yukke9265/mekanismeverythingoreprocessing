package com.yukke9265.mek_eop.jei;

import java.util.List;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.util.OriginHelper;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;

/**
 * 特殊クラフト（変換・復元）を JEI に説明用として載せる。
 * <p>
 * CustomRecipe は JEI の通常クラフト欄に出ないので、見本レイアウトを別カテゴリで見せる。
 */
@JeiPlugin
public class ModJeiPlugin implements IModPlugin {

    private static final String MODID = MekanismEverythingOreProcessing.MODID;

    @Override
    public ResourceLocation getPluginUid() {
        return MekanismEverythingOreProcessing.rl("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new SpecialCraftingRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        ItemStack diamond = new ItemStack(Items.DIAMOND);
        ItemStack blankRaw = new ItemStack(ModItems.EVERYTHING_RAW_ORE.get());
        ItemStack convertedRaw = OriginHelper.withOriginFrom(new ItemStack(ModItems.EVERYTHING_RAW_ORE.get(), 8), diamond);
        ItemStack convertedIngot = OriginHelper.withOriginFrom(new ItemStack(ModItems.EVERYTHING_INGOT.get()), diamond);

        ItemStack[] convertInputs = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            convertInputs[i] = i == 4 ? blankRaw.copy() : diamond.copy();
        }

        ItemStack[] restoreInputs = new ItemStack[9];
        for (int i = 0; i < 9; i++) {
            restoreInputs[i] = ItemStack.EMPTY;
        }
        restoreInputs[4] = convertedIngot;

        registration.addRecipes(SpecialCraftingJeiRecipe.TYPE, List.of(
                new SpecialCraftingJeiRecipe(
                        MekanismEverythingOreProcessing.rl("jei/convert_to_raw_ore"),
                        convertInputs,
                        convertedRaw,
                        "jei." + MODID + ".convert.info"),
                new SpecialCraftingJeiRecipe(
                        MekanismEverythingOreProcessing.rl("jei/restore"),
                        restoreInputs,
                        diamond.copy(),
                        "jei." + MODID + ".restore.info")
        ));
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.CRAFTING_TABLE), SpecialCraftingJeiRecipe.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.EVERYTHING_RAW_ORE.get()), SpecialCraftingJeiRecipe.TYPE);
        registration.addRecipeCatalyst(new ItemStack(ModItems.EVERYTHING_INGOT.get()), SpecialCraftingJeiRecipe.TYPE);
    }
}
