package com.yukke9265.mek_eop.component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;

/**
 * 「なんでも○○」が覚えている元アイテム。
 * <p>
 * original: 元アイテム（常に count=1）。設定が ITEM_ID のときは Component を持たない素の ItemStack。
 * power: ワールド固有ソルトを混ぜたハッシュ値。ティント色など「元アイテムごとの一意な数値」に使う。
 * <p>
 * ItemStack は equals を実装していないので、スタック可否判定のために equals/hashCode を自前で定義する。
 */
public record OriginalItem(ItemStack original, int power) {

    public static final Codec<OriginalItem> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStack.SINGLE_ITEM_CODEC.fieldOf("item").forGetter(OriginalItem::original),
            Codec.INT.fieldOf("power").forGetter(OriginalItem::power)
    ).apply(instance, OriginalItem::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OriginalItem> STREAM_CODEC = StreamCodec.composite(
            ItemStack.STREAM_CODEC, OriginalItem::original,
            ByteBufCodecs.VAR_INT, OriginalItem::power,
            OriginalItem::new
    );

    public OriginalItem {
        // 復元時に count がずれないよう、必ず 1 個に揃えておく
        original = original.copyWithCount(1);
    }

    /** 元アイテムのコピーを返す（呼び出し側が自由に count を変えられる）。 */
    public ItemStack restore() {
        return original.copy();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof OriginalItem other)) {
            return false;
        }
        return power == other.power && ItemStack.isSameItemSameComponents(original, other.original);
    }

    @Override
    public int hashCode() {
        return 31 * ItemStack.hashItemAndComponents(original) + power;
    }
}
