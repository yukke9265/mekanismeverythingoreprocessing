package com.yukke9265.mek_eop.chemical;

import org.jetbrains.annotations.Nullable;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;

/**
 * なんでもスラリー。
 * <p>
 * 対応する元アイテム（sourceItem）を 1 つだけ持つ。null のときは「対応不明」の汎用スラリー。
 * 表示名は「"Diamond" Dirty Slurry」のように、アイテム名と同じ「"元アイテム" ～」形式。
 */
public class EverythingSlurry extends Chemical {

    @Nullable
    private final Item sourceItem;
    private final boolean dirty;

    public EverythingSlurry(ChemicalBuilder builder, @Nullable Item sourceItem, boolean dirty) {
        super(builder);
        this.sourceItem = sourceItem;
        this.dirty = dirty;
    }

    @Nullable
    public Item getSourceItem() {
        return sourceItem;
    }

    public boolean isDirty() {
        return dirty;
    }

    @Override
    public Component getTextComponent() {
        String key = dirty
                ? "chemical.mekanismeverythingoreprocessing.dirty_everything_slurry"
                : "chemical.mekanismeverythingoreprocessing.clean_everything_slurry";
        if (sourceItem == null) {
            return Component.translatable(key + ".unknown");
        }
        return Component.translatable(key, sourceItem.getDescription());
    }
}
