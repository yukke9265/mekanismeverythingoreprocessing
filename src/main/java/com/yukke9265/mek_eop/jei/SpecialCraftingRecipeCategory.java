package com.yukke9265.mek_eop.jei;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModItems;

import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/** 特殊クラフト（変換・復元）の説明用カテゴリ。3x3 + 出力。 */
public class SpecialCraftingRecipeCategory implements IRecipeCategory<SpecialCraftingJeiRecipe> {

    private static final int WIDTH = 116;
    private static final int HEIGHT = 72;
    private static final int GRID_X = 0;
    private static final int GRID_Y = 0;
    private static final int OUTPUT_X = 90;
    private static final int OUTPUT_Y = 18;
    private static final int INFO_Y = 56;
    private static final int INFO_COLOR = 0x404040;

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slot;
    private final IDrawable arrow;

    public SpecialCraftingRecipeCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(WIDTH, HEIGHT);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(ModItems.EVERYTHING_RAW_ORE.get()));
        this.slot = guiHelper.getSlotDrawable();
        this.arrow = guiHelper.getRecipeArrow();
    }

    @Override
    public RecipeType<SpecialCraftingJeiRecipe> getRecipeType() {
        return SpecialCraftingJeiRecipe.TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei." + MekanismEverythingOreProcessing.MODID + ".special_crafting");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Nullable
    @Override
    public ResourceLocation getRegistryName(SpecialCraftingJeiRecipe recipe) {
        return recipe.id();
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, SpecialCraftingJeiRecipe recipe, IFocusGroup focuses) {
        ItemStack[] inputs = recipe.inputs();
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                int index = row * 3 + col;
                int x = GRID_X + col * 18;
                int y = GRID_Y + row * 18;
                var slotBuilder = builder.addSlot(RecipeIngredientRole.INPUT, x + 1, y + 1);
                ItemStack stack = inputs[index];
                if (!stack.isEmpty()) {
                    slotBuilder.addItemStack(stack);
                }
            }
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, OUTPUT_X + 1, OUTPUT_Y + 1)
                .addItemStack(recipe.output());
    }

    @Override
    public void draw(SpecialCraftingJeiRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics,
            double mouseX, double mouseY) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 3; col++) {
                slot.draw(guiGraphics, GRID_X + col * 18, GRID_Y + row * 18);
            }
        }
        slot.draw(guiGraphics, OUTPUT_X, OUTPUT_Y);
        arrow.draw(guiGraphics, 66, 19);

        // 説明文はパネル幅で折り返す（はみ出し防止）
        Minecraft minecraft = Minecraft.getInstance();
        var lines = minecraft.font.split(Component.translatable(recipe.infoKey()), WIDTH);
        int y = INFO_Y;
        for (var line : lines) {
            guiGraphics.drawString(minecraft.font, line, 0, y, INFO_COLOR, false);
            y += minecraft.font.lineHeight;
        }
    }
}
