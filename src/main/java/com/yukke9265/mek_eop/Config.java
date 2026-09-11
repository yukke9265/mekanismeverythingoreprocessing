package com.yukke9265.mek_eop;

import java.util.List;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.ModConfigSpec;

/**
 * 設定値。
 * <p>
 * STARTUP: 登録イベント（化学物質の動的登録）より前に必要な値。
 * COMMON: ゲーム中の挙動を決める値。
 */
public final class Config {
    private Config() {
    }

    /** 元アイテムの保存形式。 */
    public enum OriginStorageMode {
        /** ItemStack 丸ごと（エンチャント等の Data Component も残す）。 */
        FULL_STACK,
        /** アイテム ID だけ（Component は捨てる）。 */
        ITEM_ID
    }

    // ---------------------------------------------------------------- STARTUP

    private static final ModConfigSpec.Builder STARTUP_BUILDER = new ModConfigSpec.Builder();

    /** スラリーを生成するアイテムの namespace 一覧。空なら（自mod・air 以外の）全アイテムが対象。 */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SLURRY_NAMESPACE_WHITELIST = STARTUP_BUILDER
            .comment("Namespaces of items that get their own Dirty/Clean Everything Slurry pair (2 chemicals each).",
                    "Empty = ALL items except air and this mod's own items. That can mean thousands of chemicals",
                    "and slower startup on large modpacks — set this list when possible.",
                    "Example for a lighter pack: [\"minecraft\", \"mekanism\"]",
                    "Items outside the list still convert/process at 1x-4x; only 5x (dissolution) falls back to the generic slurry.")
            .defineListAllowEmpty("slurryNamespaceWhitelist", List.of(), () -> "", Config::isValidNamespace);

    public static final ModConfigSpec STARTUP_SPEC = STARTUP_BUILDER.build();

    // ---------------------------------------------------------------- COMMON

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<OriginStorageMode> ORIGIN_STORAGE_MODE = COMMON_BUILDER
            .comment("How the original item is stored inside Everything items.",
                    "FULL_STACK keeps data components (enchantments etc.).",
                    "ITEM_ID keeps only the item type (drops nested origins / components).",
                    "ITEM_ID forces nesting off regardless of allowNesting.")
            .defineEnum("originStorageMode", OriginStorageMode.FULL_STACK);

    public static final ModConfigSpec.BooleanValue ALLOW_NESTING = COMMON_BUILDER
            .comment("Allow converting Everything items that already have an origin (nesting).",
                    "Default false. Ignored (treated as false) when originStorageMode is ITEM_ID,",
                    "because ITEM_ID cannot preserve nested original_item components.",
                    "Even when true, Chemical Dissolution still rejects nested items (slurry cannot store nesting).")
            .define("allowNesting", false);

    public static final ModConfigSpec.IntValue MAX_NESTING_DEPTH = COMMON_BUILDER
            .comment("Maximum nesting depth when nesting is effectively allowed (see allowNesting).")
            .defineInRange("maxNestingDepth", 4, 1, 32);

    public static final ModConfigSpec.ConfigValue<List<? extends String>> BLACKLIST = COMMON_BUILDER
            .comment("Item ids that can never be converted into Everything Raw Ore.",
                    "The item tag mekanismeverythingoreprocessing:blacklist is also respected.")
            .defineListAllowEmpty("blacklist", List.of(), () -> "minecraft:air", Config::isValidItemId);

    public static final ModConfigSpec COMMON_SPEC = COMMON_BUILDER.build();

    // ---------------------------------------------------------------- helpers

    private static boolean isValidNamespace(Object value) {
        if (!(value instanceof String text)) {
            return false;
        }
        return ResourceLocation.isValidNamespace(text);
    }

    private static boolean isValidItemId(Object value) {
        if (!(value instanceof String text)) {
            return false;
        }
        return ResourceLocation.tryParse(text) != null;
    }

    /** Config のブラックリストに含まれるアイテムか。 */
    public static boolean isBlacklistedById(ResourceLocation itemId) {
        for (String entry : BLACKLIST.get()) {
            if (itemId.toString().equals(entry)) {
                return true;
            }
        }
        return false;
    }

    /**
     * このアイテムに個別スラリーを生成するか（STARTUP 設定）。
     * <p>
     * whitelist が空なら全 namespace対象。絞ると起動が軽くなる代わりに、
     * 対象外アイテムの 5x は汎用スラリー（元情報なし結晶）になる。
     */
    public static boolean isSlurryTarget(ResourceLocation itemId) {
        List<? extends String> whitelist = SLURRY_NAMESPACE_WHITELIST.get();
        if (whitelist.isEmpty()) {
            return true;
        }
        return whitelist.contains(itemId.getNamespace());
    }

    /** whitelist が空（＝全登録モード）か。ログ警告用。 */
    public static boolean isSlurryWhitelistEmpty() {
        return SLURRY_NAMESPACE_WHITELIST.get().isEmpty();
    }

    /**
     * 入れ子変換が実際に許されるか。
     * <p>
     * ITEM_ID モードでは内側の original_item を保存できないので、設定値に関わらず常に false。
     */
    public static boolean isNestingEffectivelyAllowed() {
        if (ORIGIN_STORAGE_MODE.get() == OriginStorageMode.ITEM_ID) {
            return false;
        }
        return ALLOW_NESTING.get();
    }
}
