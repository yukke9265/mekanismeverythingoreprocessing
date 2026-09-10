package com.yukke9265.mek_eop.item;

import java.util.List;

import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;

/** なんでもブロックの BlockItem。表示名とツールチップは EverythingItem と同じ扱い。 */
public class EverythingBlockItem extends BlockItem {

    public EverythingBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        OriginalItem origin = OriginHelper.getOrigin(stack);
        if (origin == null) {
            return super.getName(stack);
        }
        return Component.translatable(getDescriptionId(stack) + ".named", origin.original().getHoverName());
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, context, tooltip, flag);
        EverythingItem.appendOriginTooltip(stack, tooltip, flag);
    }
}
