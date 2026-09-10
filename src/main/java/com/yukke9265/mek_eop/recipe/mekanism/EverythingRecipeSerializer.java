package com.yukke9265.mek_eop.recipe.mekanism;

import java.util.function.BiFunction;

import com.mojang.datafixers.util.Function3;
import com.mojang.datafixers.util.Function4;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import mekanism.api.SerializationConstants;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.recipes.ChemicalDissolutionRecipe;
import mekanism.api.recipes.FluidChemicalToChemicalRecipe;
import mekanism.api.recipes.ItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.basic.BasicChemicalCrystallizerRecipe;
import mekanism.api.recipes.basic.BasicChemicalDissolutionRecipe;
import mekanism.api.recipes.basic.BasicItemStackChemicalToItemStackRecipe;
import mekanism.api.recipes.basic.BasicItemStackToItemStackRecipe;
import mekanism.api.recipes.basic.BasicWashingRecipe;
import mekanism.api.recipes.ingredients.ChemicalStackIngredient;
import mekanism.api.recipes.ingredients.FluidStackIngredient;
import mekanism.api.recipes.ingredients.ItemStackIngredient;
import mekanism.api.recipes.ingredients.creator.IngredientCreatorAccess;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeSerializer;

/**
 * Mekanism の Basic レシピと同じ JSON 形式を読み書きするシリアライザ。
 * <p>
 * JSON の形は Mekanism 本家と同じにしておき、生成するクラスだけ「元情報をコピーする派生クラス」に差し替える。
 * factory に派生クラスのコンストラクタを渡して使う。
 */
public record EverythingRecipeSerializer<RECIPE extends Recipe<?>>(MapCodec<RECIPE> codec, StreamCodec<RegistryFriendlyByteBuf, RECIPE> streamCodec)
        implements RecipeSerializer<RECIPE> {

    /** Enriching / Crushing / Smelting 用（アイテム → アイテム）。 */
    public static <RECIPE extends BasicItemStackToItemStackRecipe> EverythingRecipeSerializer<RECIPE> itemToItem(
            BiFunction<ItemStackIngredient, ItemStack, RECIPE> factory) {
        return new EverythingRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.INPUT).forGetter(BasicItemStackToItemStackRecipe::getInput),
                ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicItemStackToItemStackRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
                ItemStackIngredient.STREAM_CODEC, BasicItemStackToItemStackRecipe::getInput,
                ItemStack.STREAM_CODEC, BasicItemStackToItemStackRecipe::getOutputRaw,
                factory
        ));
    }

    /** Purifying / Injecting 用（アイテム + 化学物質 → アイテム）。 */
    public static <RECIPE extends BasicItemStackChemicalToItemStackRecipe> EverythingRecipeSerializer<RECIPE> itemChemicalToItem(
            Function4<ItemStackIngredient, ChemicalStackIngredient, ItemStack, Boolean, RECIPE> factory) {
        return new EverythingRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.ITEM_INPUT).forGetter(ItemStackChemicalToItemStackRecipe::getItemInput),
                IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(ItemStackChemicalToItemStackRecipe::getChemicalInput),
                ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicItemStackChemicalToItemStackRecipe::getOutputRaw),
                Codec.BOOL.fieldOf(SerializationConstants.PER_TICK_USAGE).forGetter(ItemStackChemicalToItemStackRecipe::perTickUsage)
        ).apply(instance, factory)), StreamCodec.composite(
                ItemStackIngredient.STREAM_CODEC, ItemStackChemicalToItemStackRecipe::getItemInput,
                IngredientCreatorAccess.chemicalStack().streamCodec(), ItemStackChemicalToItemStackRecipe::getChemicalInput,
                ItemStack.STREAM_CODEC, BasicItemStackChemicalToItemStackRecipe::getOutputRaw,
                ByteBufCodecs.BOOL, ItemStackChemicalToItemStackRecipe::perTickUsage,
                factory
        ));
    }

    /** Dissolution 用（アイテム + 化学物質 → 化学物質）。 */
    public static EverythingRecipeSerializer<BasicChemicalDissolutionRecipe> dissolution(
            Function4<ItemStackIngredient, ChemicalStackIngredient, ChemicalStack, Boolean, BasicChemicalDissolutionRecipe> factory) {
        return new EverythingRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                ItemStackIngredient.CODEC.fieldOf(SerializationConstants.ITEM_INPUT).forGetter(ChemicalDissolutionRecipe::getItemInput),
                IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(ChemicalDissolutionRecipe::getChemicalInput),
                ChemicalStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicChemicalDissolutionRecipe::getOutputRaw),
                Codec.BOOL.fieldOf(SerializationConstants.PER_TICK_USAGE).forGetter(BasicChemicalDissolutionRecipe::perTickUsage)
        ).apply(instance, factory)), StreamCodec.composite(
                ItemStackIngredient.STREAM_CODEC, BasicChemicalDissolutionRecipe::getItemInput,
                IngredientCreatorAccess.chemicalStack().streamCodec(), BasicChemicalDissolutionRecipe::getChemicalInput,
                ChemicalStack.STREAM_CODEC, BasicChemicalDissolutionRecipe::getOutputRaw,
                ByteBufCodecs.BOOL, BasicChemicalDissolutionRecipe::perTickUsage,
                factory
        ));
    }

    /** Washing 用（液体 + 化学物質 → 化学物質）。 */
    public static EverythingRecipeSerializer<BasicWashingRecipe> washing(
            Function3<FluidStackIngredient, ChemicalStackIngredient, ChemicalStack, BasicWashingRecipe> factory) {
        return new EverythingRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                FluidStackIngredient.CODEC.fieldOf(SerializationConstants.FLUID_INPUT).forGetter(FluidChemicalToChemicalRecipe::getFluidInput),
                IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.CHEMICAL_INPUT).forGetter(FluidChemicalToChemicalRecipe::getChemicalInput),
                ChemicalStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicWashingRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
                FluidStackIngredient.STREAM_CODEC, FluidChemicalToChemicalRecipe::getFluidInput,
                IngredientCreatorAccess.chemicalStack().streamCodec(), FluidChemicalToChemicalRecipe::getChemicalInput,
                ChemicalStack.STREAM_CODEC, BasicWashingRecipe::getOutputRaw,
                factory
        ));
    }

    /** Crystallizing 用（化学物質 → アイテム）。 */
    public static EverythingRecipeSerializer<BasicChemicalCrystallizerRecipe> crystallizing(
            BiFunction<ChemicalStackIngredient, ItemStack, BasicChemicalCrystallizerRecipe> factory) {
        return new EverythingRecipeSerializer<>(RecordCodecBuilder.mapCodec(instance -> instance.group(
                IngredientCreatorAccess.chemicalStack().codec().fieldOf(SerializationConstants.INPUT).forGetter(BasicChemicalCrystallizerRecipe::getInput),
                ItemStack.CODEC.fieldOf(SerializationConstants.OUTPUT).forGetter(BasicChemicalCrystallizerRecipe::getOutputRaw)
        ).apply(instance, factory)), StreamCodec.composite(
                IngredientCreatorAccess.chemicalStack().streamCodec(), BasicChemicalCrystallizerRecipe::getInput,
                ItemStack.STREAM_CODEC, BasicChemicalCrystallizerRecipe::getOutputRaw,
                factory
        ));
    }
}
