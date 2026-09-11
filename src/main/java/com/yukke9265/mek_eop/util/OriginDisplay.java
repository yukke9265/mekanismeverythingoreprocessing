package com.yukke9265.mek_eop.util;

import java.util.List;

import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.item.EverythingBlockItem;
import com.yukke9265.mek_eop.item.EverythingItem;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * なんでも○○の表示名とツールチップ。
 * <p>
 * Item / BlockItem の両方から同じ見た目になるように、ここに寄せる。
 */
public final class OriginDisplay {
    private OriginDisplay() {
    }

    /** 元情報があれば「"元アイテム" ○○」、なければ通常名。 */
    public static Component name(ItemStack stack, Component fallback) {
        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            return fallback;
        }
        return Component.translatable(stack.getItem().getDescriptionId(stack) + ".named", origin.original().getHoverName());
    }

    /** ツールチップに元アイテム・入れ子深さ・（F3+H 時）power を足す。 */
    public static void appendTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            tooltip.add(Component.translatable("tooltip.mekanismeverythingoreprocessing.no_origin").withStyle(ChatFormatting.DARK_GRAY));
            return;
        }
        tooltip.add(Component.translatable("tooltip.mekanismeverythingoreprocessing.origin", origin.original().getHoverName())
                .withStyle(ChatFormatting.GRAY));
        int depth = OriginHelper.nestingDepth(stack);
        if (depth > 1) {
            tooltip.add(Component.translatable("tooltip.mekanismeverythingoreprocessing.nesting", depth).withStyle(ChatFormatting.GRAY));
        }
        if (flag.isAdvanced()) {
            tooltip.add(Component.translatable("tooltip.mekanismeverythingoreprocessing.power",
                    String.format("%08X", origin.power())).withStyle(ChatFormatting.DARK_GRAY));
        }
    }

    /** なんでも○○（BlockItem 含む）かどうか。描画の再帰判定に使う。 */
    public static boolean isEverythingItem(ItemStack stack) {
        return !stack.isEmpty()
                && (stack.getItem() instanceof EverythingItem || stack.getItem() instanceof EverythingBlockItem);
    }

    /**
     * アイコン表示用に、入れ子のなんでも○○をほどいて「普通のアイテム」まで辿る。
     * <p>
     * なんでも○○自体を ItemRenderer に渡すと BEWLR が再帰してクラッシュしうるため。
     * 空白（元情報なし）のなんでも○○だけが残ったら EMPTY（アイコン無し）。
     */
    public static ItemStack iconStack(ItemStack original) {
        ItemStack current = original;
        for (int i = 0; i < 16; i++) {
            if (!isEverythingItem(current)) {
                return current;
            }
            OriginalItem origin = OriginHelper.getOrigin(current);
            if (origin == null) {
                return ItemStack.EMPTY;
            }
            current = origin.original();
        }
        return ItemStack.EMPTY;
    }
}
