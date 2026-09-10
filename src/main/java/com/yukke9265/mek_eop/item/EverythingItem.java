package com.yukke9265.mek_eop.item;

import java.util.List;

import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * なんでも○○系アイテムの共通クラス。
 * <p>
 * 表示名に元アイテム名を添え、ツールチップに元アイテム・power・入れ子の深さを出す。
 * 加工ロジックは持たない（レシピ側で Component をコピーする）。
 */
public class EverythingItem extends Item {

    public EverythingItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            return super.getName(stack);
        }
        // 例: "Everything Ingot (Diamond)"
        return Component.translatable(getDescriptionId(stack) + ".named", origin.original().getHoverName());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        appendOriginTooltip(stack, tooltip, flag);
    }

    /** BlockItem 側からも使えるように static にしておく。 */
    public static void appendOriginTooltip(ItemStack stack, List<Component> tooltip, TooltipFlag flag) {
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
}
