package com.yukke9265.mek_eop.datagen;

import java.util.concurrent.CompletableFuture;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.recipe.crafting.ConvertToRawOreRecipe;
import com.yukke9265.mek_eop.recipe.crafting.OriginPackRecipe;
import com.yukke9265.mek_eop.recipe.crafting.RestoreRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingCrystallizingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingDissolutionRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingWashingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyCrushingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyEnrichingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyInjectingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyPurifyingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopySmeltingRecipe;
import com.yukke9265.mek_eop.recipe.vanilla.OriginCopyBlastFurnaceRecipe;
import com.yukke9265.mek_eop.recipe.vanilla.OriginCopyFurnaceRecipe;
import com.yukke9265.mek_eop.registry.ModItems;
import com.yukke9265.mek_eop.registry.ModTags;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CookingBookCategory;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.registries.DeferredHolder;

/**
 * レシピ JSON の生成。
 * <p>
 * 比率は Mekanism 本家の原石（raw ore）処理と同じにする:
 * 原石3 → ダスト4 / 原石1 + O2 → クランプ2 / 原石3 + HCl → シャード8 / 原石3 + H2SO4 → スラリー 2000mB → 結晶10。
 * <p>
 * Mekanism のレシピはビルダーが本体側にしかないので、レシピオブジェクトを直接作って出力する。
 */
public class ModRecipeProvider extends RecipeProvider {

    private static final Holder<Chemical> OXYGEN = mekanismChemical("oxygen");
    private static final Holder<Chemical> HYDROGEN_CHLORIDE = mekanismChemical("hydrogen_chloride");
    private static final Holder<Chemical> SULFURIC_ACID = mekanismChemical("sulfuric_acid");

    public ModRecipeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider);
    }

    @Override
    protected void buildRecipes(RecipeOutput output) {
        buildCraftingRecipes(output);
        buildFurnaceRecipes(output);
        buildItemProcessingRecipes(output);
        buildSlurryRecipes(output);
    }

    // ------------------------------------------------------------ クラフト

    private void buildCraftingRecipes(RecipeOutput output) {
        save(output, "crafting/convert_to_raw_ore", new ConvertToRawOreRecipe(CraftingBookCategory.MISC));
        save(output, "crafting/restore", new RestoreRecipe(CraftingBookCategory.MISC));

        // 元情報を保ったままの詰め替え（ナゲット → インゴット、インゴット ⇄ ブロック）
        // 注意: 「インゴット1個 → ナゲット9個」は復元レシピ（インゴット単独）と入力が同じで衝突するため用意しない
        save(output, "crafting/ingot_from_nuggets", pack(ModItems.EVERYTHING_NUGGET, 9, ModItems.EVERYTHING_INGOT, 1));
        save(output, "crafting/block_from_ingots", pack(ModItems.EVERYTHING_INGOT, 9, ModItems.EVERYTHING_BLOCK, 1));
        save(output, "crafting/ingots_from_block", pack(ModItems.EVERYTHING_BLOCK, 1, ModItems.EVERYTHING_INGOT, 9));
    }

    private static OriginPackRecipe pack(DeferredHolder<?, ? extends ItemLike> from, int fromCount, DeferredHolder<?, ? extends ItemLike> to, int toCount) {
        return new OriginPackRecipe(from.get().asItem().builtInRegistryHolder(), fromCount, to.get().asItem().builtInRegistryHolder(), toCount);
    }

    // ------------------------------------------------------------ バニラかまど / 溶鉱炉

    private void buildFurnaceRecipes(RecipeOutput output) {
        saveCooking(output, "ingot_from_raw_ore", ModItems.EVERYTHING_RAW_ORE, 0.7f);
        saveCooking(output, "ingot_from_dust", ModItems.EVERYTHING_DUST, 0.35f);
    }

    private void saveCooking(RecipeOutput output, String name, ItemLike input, float experience) {
        Ingredient ingredient = Ingredient.of(input);
        ItemStack ingot = new ItemStack(ModItems.EVERYTHING_INGOT.get());
        save(output, "furnace/" + name + "_smelting",
                new OriginCopyFurnaceRecipe("", CookingBookCategory.MISC, ingredient, ingot, experience, 200));
        save(output, "furnace/" + name + "_blasting",
                new OriginCopyBlastFurnaceRecipe("", CookingBookCategory.MISC, ingredient, ingot, experience, 100));
    }

    // ------------------------------------------------------------ Mekanism アイテム系（1x〜4x）

    private void buildItemProcessingRecipes(RecipeOutput output) {
        ItemStackIngredient rawOre = item(ModItems.EVERYTHING_RAW_ORE, 1);
        ItemStackIngredient rawOre3 = item(ModItems.EVERYTHING_RAW_ORE, 3);

        // Energized Smelter: 原石 → インゴット、ダスト → インゴット
        save(output, "processing/ingot/from_raw_ore", new OriginCopySmeltingRecipe(rawOre, stack(ModItems.EVERYTHING_INGOT, 1)));
        save(output, "processing/ingot/from_dust", new OriginCopySmeltingRecipe(item(ModItems.EVERYTHING_DUST, 1), stack(ModItems.EVERYTHING_INGOT, 1)));

        // Enrichment Chamber: 原石3 → ダスト4、ダーティダスト → ダスト
        save(output, "processing/dust/from_raw_ore", new OriginCopyEnrichingRecipe(rawOre3, stack(ModItems.EVERYTHING_DUST, 4)));
        save(output, "processing/dust/from_dirty_dust", new OriginCopyEnrichingRecipe(item(ModItems.EVERYTHING_DIRTY_DUST, 1), stack(ModItems.EVERYTHING_DUST, 1)));

        // Crusher: インゴット → ダスト、クランプ → ダーティダスト
        save(output, "processing/dust/from_ingot", new OriginCopyCrushingRecipe(item(ModItems.EVERYTHING_INGOT, 1), stack(ModItems.EVERYTHING_DUST, 1)));
        save(output, "processing/dirty_dust/from_clump", new OriginCopyCrushingRecipe(item(ModItems.EVERYTHING_CLUMP, 1), stack(ModItems.EVERYTHING_DIRTY_DUST, 1)));

        // Purification Chamber: 原石 + O2 → クランプ2、シャード + O2 → クランプ
        ChemicalStackIngredient oxygen = chemical(OXYGEN, 1);
        save(output, "processing/clump/from_raw_ore", new OriginCopyPurifyingRecipe(rawOre, oxygen, stack(ModItems.EVERYTHING_CLUMP, 2), true));
        save(output, "processing/clump/from_shard", new OriginCopyPurifyingRecipe(item(ModItems.EVERYTHING_SHARD, 1), oxygen, stack(ModItems.EVERYTHING_CLUMP, 1), true));

        // Chemical Injection Chamber: 原石3 + HCl → シャード8、結晶 + HCl → シャード
        ChemicalStackIngredient hydrogenChloride = chemical(HYDROGEN_CHLORIDE, 1);
        save(output, "processing/shard/from_raw_ore", new OriginCopyInjectingRecipe(rawOre3, hydrogenChloride, stack(ModItems.EVERYTHING_SHARD, 8), true));
        save(output, "processing/shard/from_crystal", new OriginCopyInjectingRecipe(item(ModItems.EVERYTHING_CRYSTAL, 1), hydrogenChloride, stack(ModItems.EVERYTHING_SHARD, 1), true));
    }

    // ------------------------------------------------------------ Mekanism スラリー系（5x）

    private void buildSlurryRecipes(RecipeOutput output) {
        // JSON 上の出力は「汎用スラリー」。実際の種類はレシピクラスが入力から決める
        Holder<Chemical> unknownDirty = EverythingSlurries.holderOf(EverythingSlurries.dirtyFor(null));
        Holder<Chemical> unknownClean = EverythingSlurries.holderOf(EverythingSlurries.cleanForDirty(unknownDirty.value()));

        // Dissolution Chamber: 原石3 + H2SO4 → ダーティスラリー 2000mB
        save(output, "processing/slurry/dirty/from_raw_ore", new EverythingDissolutionRecipe(
                item(ModItems.EVERYTHING_RAW_ORE, 3), chemical(SULFURIC_ACID, 1), new ChemicalStack(unknownDirty, 2_000), true));

        // Chemical Washer: 水5 + ダーティ1 → クリーン1
        save(output, "processing/slurry/clean", new EverythingWashingRecipe(
                IngredientCreatorAccess.fluid().from(FluidTags.WATER, 5),
                IngredientCreatorAccess.chemicalStack().from(ModTags.Chemicals.DIRTY_EVERYTHING_SLURRY, 1L),
                new ChemicalStack(unknownClean, 1)));

        // Chemical Crystallizer: クリーン 200mB → 結晶1
        save(output, "processing/crystal/from_slurry", new EverythingCrystallizingRecipe(
                IngredientCreatorAccess.chemicalStack().from(ModTags.Chemicals.CLEAN_EVERYTHING_SLURRY, 200L),
                stack(ModItems.EVERYTHING_CRYSTAL, 1)));
    }

    // ------------------------------------------------------------ helpers

    private static void save(RecipeOutput output, String path, Recipe<?> recipe) {
        ResourceLocation id = MekanismEverythingOreProcessing.rl(path);
        output.accept(id, recipe, null);
    }

    private static ItemStackIngredient item(ItemLike item, int count) {
        return IngredientCreatorAccess.item().from(item, count);
    }

    private static ItemStack stack(ItemLike item, int count) {
        return new ItemStack(item, count);
    }

    private static ChemicalStackIngredient chemical(Holder<Chemical> chemical, long amount) {
        return IngredientCreatorAccess.chemicalStack().fromHolder(chemical, amount);
    }

    private static Holder<Chemical> mekanismChemical(String name) {
        return DeferredHolder.create(MekanismAPI.CHEMICAL_REGISTRY_NAME, ResourceLocation.fromNamespaceAndPath(MekanismAPI.MEKANISM_MODID, name));
    }
}
