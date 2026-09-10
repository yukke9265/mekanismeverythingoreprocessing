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

    /** スラリーを生成するアイテムの namespace 一覧。空なら全アイテムが対象。 */
    public static final ModConfigSpec.ConfigValue<List<? extends String>> SLURRY_NAMESPACE_WHITELIST = STARTUP_BUILDER
            .comment("Namespaces of items that get their own Everything Slurry pair.",
                    "Empty = every registered item. Restrict this on huge modpacks to reduce registry size.")
            .defineListAllowEmpty("slurryNamespaceWhitelist", List.of(), () -> "", Config::isValidNamespace);

    public static final ModConfigSpec STARTUP_SPEC = STARTUP_BUILDER.build();

    // ---------------------------------------------------------------- COMMON

    private static final ModConfigSpec.Builder COMMON_BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.EnumValue<OriginStorageMode> ORIGIN_STORAGE_MODE = COMMON_BUILDER
            .comment("How the original item is stored inside Everything items.",
                    "FULL_STACK keeps data components (enchantments etc.), ITEM_ID keeps only the item type.")
            .defineEnum("originStorageMode", OriginStorageMode.FULL_STACK);

    public static final ModConfigSpec.BooleanValue ALLOW_NESTING = COMMON_BUILDER
            .comment("Allow converting Everything items themselves (Everything Raw Ore of Everything Raw Ore ...).")
            .define("allowNesting", true);

    public static final ModConfigSpec.IntValue MAX_NESTING_DEPTH = COMMON_BUILDER
            .comment("Maximum nesting depth when allowNesting is true.")
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

    /** このアイテムにスラリーを生成するか（STARTUP 設定）。 */
    public static boolean isSlurryTarget(ResourceLocation itemId) {
        List<? extends String> whitelist = SLURRY_NAMESPACE_WHITELIST.get();
        if (whitelist.isEmpty()) {
            return true;
        }
        return whitelist.contains(itemId.getNamespace());
    }
}
