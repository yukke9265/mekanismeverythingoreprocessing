package com.yukke9265.mek_eop.util;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.mek_eop.Config;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModDataComponents;
import com.yukke9265.mek_eop.registry.ModTags;
import com.yukke9265.mek_eop.world.WorldSaltData;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

/**
 * 元アイテム（OriginalItem）の生成・取得・判定をまとめたヘルパー。
 * <p>
 * レシピや機械からはこのクラスだけを呼べばよいようにしておく。
 */
public final class OriginHelper {
    private OriginHelper() {
    }

    // ------------------------------------------------------------ 取得

    /** スタックが持つ元アイテム情報。無ければ null。 */
    @Nullable
    public static OriginalItem getOrigin(ItemStack stack) {
        return stack.get(ModDataComponents.ORIGINAL_ITEM);
    }

    public static boolean hasOrigin(ItemStack stack) {
        return stack.has(ModDataComponents.ORIGINAL_ITEM);
    }

    /** from の元アイテム情報を to にコピーする（to をそのまま返す）。 */
    public static ItemStack copyOrigin(ItemStack from, ItemStack to) {
        OriginalItem origin = getOrigin(from);
        if (origin != null) {
            to.set(ModDataComponents.ORIGINAL_ITEM, origin);
        }
        return to;
    }

    // ------------------------------------------------------------ 生成

    /**
     * 変換元アイテムから OriginalItem を作る（サーバー側専用）。
     * <p>
     * 保存形式は Config に従う。power はワールドソルトを混ぜて計算する。
     * サーバーが取れない状況（クライアントの表示用など）では power=0 で作る。
     */
    public static OriginalItem createOrigin(ItemStack source) {
        ItemStack original;
        if (Config.ORIGIN_STORAGE_MODE.get() == Config.OriginStorageMode.ITEM_ID) {
            original = new ItemStack(source.getItem());
        } else {
            original = source.copyWithCount(1);
        }

        int power = 0;
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            power = computePower(source.getItem(), WorldSaltData.getSalt(server));
        }
        return new OriginalItem(original, power);
    }

    /** target に source 由来の OriginalItem を付けて返す。 */
    public static ItemStack withOriginFrom(ItemStack target, ItemStack source) {
        target.set(ModDataComponents.ORIGINAL_ITEM, createOrigin(source));
        return target;
    }

    // ------------------------------------------------------------ 判定

    /** 変換（なんでも原石化）してよいアイテムか。 */
    public static boolean canConvert(ItemStack source) {
        if (source.isEmpty()) {
            return false;
        }
        ResourceLocation id = idOf(source.getItem());
        if (Config.isBlacklistedById(id) || source.is(ModTags.Items.BLACKLIST)) {
            return false;
        }
        // 入れ子（なんでも○○をさらに変換）の制限
        if (hasOrigin(source)) {
            if (!Config.ALLOW_NESTING.get()) {
                return false;
            }
            return nestingDepth(source) < Config.MAX_NESTING_DEPTH.get();
        }
        return true;
    }

    /** 入れ子の深さ。元情報を持たないアイテムは 0。 */
    public static int nestingDepth(ItemStack stack) {
        int depth = 0;
        OriginalItem origin = getOrigin(stack);
        while (origin != null) {
            depth++;
            origin = getOrigin(origin.original());
        }
        return depth;
    }

    // ------------------------------------------------------------ power

    public static ResourceLocation idOf(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    /**
     * レジストリ名とワールドソルトから power を計算する。
     * <p>
     * String.hashCode は Java 仕様で固定なので、同じ ID・同じソルトなら必ず同じ値になる。
     * 後半は splitmix64 の仕上げ処理で、ビットをよく混ぜるためのもの。
     */
    public static int computePower(Item item, long salt) {
        long h = idOf(item).toString().hashCode() ^ salt;
        h = (h ^ (h >>> 30)) * 0xBF58476D1CE4E5B9L;
        h = (h ^ (h >>> 27)) * 0x94D049BB133111EBL;
        h = h ^ (h >>> 31);
        return (int) h;
    }

    /**
     * power からティント色（0xRRGGBB）を作る。
     * <p>
     * 色相は power から取り、彩度・明度は固定にして「灰色ベースの上に乗せても見やすい色」にする。
     */
    public static int tintColor(int power) {
        float hue = (power & 0xFFFF) / 65536.0f;
        return Mth.hsvToRgb(hue, 0.55f, 0.95f);
    }
}
