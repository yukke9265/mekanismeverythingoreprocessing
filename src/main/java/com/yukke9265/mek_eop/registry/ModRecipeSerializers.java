package com.yukke9265.mek_eop.registry;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.recipe.crafting.ConvertToRawOreRecipe;
import com.yukke9265.mek_eop.recipe.crafting.OriginPackRecipe;
import com.yukke9265.mek_eop.recipe.crafting.RestoreRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingCrystallizingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingDissolutionRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingRecipeSerializer;
import com.yukke9265.mek_eop.recipe.mekanism.EverythingWashingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyCrushingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyEnrichingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyInjectingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopyPurifyingRecipe;
import com.yukke9265.mek_eop.recipe.mekanism.OriginCopySmeltingRecipe;
import com.yukke9265.mek_eop.recipe.vanilla.OriginCopyBlastFurnaceRecipe;
import com.yukke9265.mek_eop.recipe.vanilla.OriginCopyFurnaceRecipe;

import mekanism.api.recipes.basic.BasicChemicalCrystallizerRecipe;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.basic.BasicCrushingRecipe;
import mekanism.api.recipes.basic.BasicEnrichingRecipe;
import mekanism.api.recipes.basic.BasicInjectingRecipe;
import mekanism.api.recipes.basic.BasicPurifyingRecipe;
import mekanism.api.recipes.basic.BasicSmeltingRecipe;
import mekanism.api.recipes.basic.BasicWashingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/**
 * レシピシリアライザの登録。
 * <p>
 * Mekanism 系は「レシピタイプは Mekanism のものを使い、シリアライザだけ自前」にする。
 * こうすると Mekanism の機械がそのまま読んでくれる。
 */
public final class ModRecipeSerializers {
    private ModRecipeSerializers() {
    }

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, MekanismEverythingOreProcessing.MODID);

    // ------------------------------------------------------------ クラフト

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<ConvertToRawOreRecipe>> CONVERT_TO_RAW_ORE =
            RECIPE_SERIALIZERS.register("convert_to_raw_ore", () -> new SimpleCraftingRecipeSerializer<>(ConvertToRawOreRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<RestoreRecipe>> RESTORE =
            RECIPE_SERIALIZERS.register("restore", () -> new SimpleCraftingRecipeSerializer<>(RestoreRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<OriginPackRecipe>> ORIGIN_PACK =
            RECIPE_SERIALIZERS.register("origin_pack", () -> new EverythingRecipeSerializer<>(OriginPackRecipe.CODEC, OriginPackRecipe.STREAM_CODEC));

    // ------------------------------------------------------------ バニラかまど

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCookingSerializer<OriginCopyFurnaceRecipe>> VANILLA_SMELTING =
            RECIPE_SERIALIZERS.register("smelting", () -> new SimpleCookingSerializer<>(OriginCopyFurnaceRecipe::new, 200));

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCookingSerializer<OriginCopyBlastFurnaceRecipe>> VANILLA_BLASTING =
            RECIPE_SERIALIZERS.register("blasting", () -> new SimpleCookingSerializer<>(OriginCopyBlastFurnaceRecipe::new, 100));

    // ------------------------------------------------------------ Mekanism（アイテム系）

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicEnrichingRecipe>> ENRICHING =
            RECIPE_SERIALIZERS.register("enriching", () -> EverythingRecipeSerializer.<BasicEnrichingRecipe>itemToItem(OriginCopyEnrichingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicCrushingRecipe>> CRUSHING =
            RECIPE_SERIALIZERS.register("crushing", () -> EverythingRecipeSerializer.<BasicCrushingRecipe>itemToItem(OriginCopyCrushingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicSmeltingRecipe>> SMELTING =
            RECIPE_SERIALIZERS.register("energized_smelting", () -> EverythingRecipeSerializer.<BasicSmeltingRecipe>itemToItem(OriginCopySmeltingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicPurifyingRecipe>> PURIFYING =
            RECIPE_SERIALIZERS.register("purifying", () -> EverythingRecipeSerializer.<BasicPurifyingRecipe>itemChemicalToItem(OriginCopyPurifyingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicInjectingRecipe>> INJECTING =
            RECIPE_SERIALIZERS.register("injecting", () -> EverythingRecipeSerializer.<BasicInjectingRecipe>itemChemicalToItem(OriginCopyInjectingRecipe::new));

    // ------------------------------------------------------------ Mekanism（スラリー系）

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicChemicalDissolutionRecipe>> DISSOLUTION =
            RECIPE_SERIALIZERS.register("dissolution", () -> EverythingRecipeSerializer.dissolution(EverythingDissolutionRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicWashingRecipe>> WASHING =
            RECIPE_SERIALIZERS.register("washing", () -> EverythingRecipeSerializer.washing(EverythingWashingRecipe::new));

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<BasicChemicalCrystallizerRecipe>> CRYSTALLIZING =
            RECIPE_SERIALIZERS.register("crystallizing", () -> EverythingRecipeSerializer.crystallizing(EverythingCrystallizingRecipe::new));

    public static void register(IEventBus modEventBus) {
        RECIPE_SERIALIZERS.register(modEventBus);
    }
}
