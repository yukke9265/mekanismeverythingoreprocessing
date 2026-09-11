package com.yukke9265.mek_eop.item;

import java.util.List;

import com.yukke9265.mek_eop.util.OriginDisplay;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** なんでもブロックの BlockItem。表示は {@link OriginDisplay} に揃える。 */
public class EverythingBlockItem extends BlockItem {

    public EverythingBlockItem(Block block, Properties properties) {
        super(block, properties);
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
