package com.yukke9265.mek_eop.item;

import java.util.List;

import com.yukke9265.mek_eop.util.OriginDisplay;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

/**
 * なんでも○○系アイテムの共通クラス。
 * <p>
 * 表示名とツールチップは {@link OriginDisplay} に任せる。加工ロジックは持たない。
 */
public class EverythingItem extends Item {

    public EverythingItem(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        return OriginDisplay.name(stack, super.getName(stack));
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        OriginDisplay.appendTooltip(stack, tooltip, flag);
    }
}
