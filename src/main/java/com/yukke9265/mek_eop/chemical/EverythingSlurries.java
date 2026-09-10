package com.yukke9265.mek_eop.chemical;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.mek_eop.Config;
import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.util.OriginHelper;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.registries.RegisterEvent;

/**
 * 全アイテム分のなんでもスラリー（Dirty / Clean）を動的に登録し、アイテム ⇄ スラリーの対応表を持つ。
 * <p>
 * 前提: Mekanism の化学物質レジストリはバニラのアイテムレジストリより後に登録イベントが来るので、
 * その時点で BuiltInRegistries.ITEM を走査できる。
 * <p>
 * ID の形: mekanismeverythingoreprocessing:dirty/<item namespace>/<item path>
 */
public final class EverythingSlurries {
    private EverythingSlurries() {
    }

    private static final Map<Item, EverythingSlurry> DIRTY_BY_ITEM = new IdentityHashMap<>();
    private static final Map<Item, EverythingSlurry> CLEAN_BY_ITEM = new IdentityHashMap<>();
    private static final List<EverythingSlurry> ALL = new ArrayList<>();

    /** 対応が取れないときに使う汎用スラリー。 */
    public static final ResourceLocation UNKNOWN_DIRTY_ID = MekanismEverythingOreProcessing.rl("dirty_unknown");
    public static final ResourceLocation UNKNOWN_CLEAN_ID = MekanismEverythingOreProcessing.rl("clean_unknown");

    private static EverythingSlurry unknownDirty;
    private static EverythingSlurry unknownClean;

    // ------------------------------------------------------------ 登録

    public static void onRegister(RegisterEvent event) {
        if (!event.getRegistryKey().equals(MekanismAPI.CHEMICAL_REGISTRY_NAME)) {
            return;
        }
        event.register(MekanismAPI.CHEMICAL_REGISTRY_NAME, helper -> {
            unknownDirty = new EverythingSlurry(ChemicalBuilder.dirtySlurry().tint(0x8C8C8C), null, true);
            unknownClean = new EverythingSlurry(ChemicalBuilder.cleanSlurry().tint(0x8C8C8C), null, false);
            helper.register(UNKNOWN_DIRTY_ID, unknownDirty);
            helper.register(UNKNOWN_CLEAN_ID, unknownClean);
            ALL.add(unknownDirty);
            ALL.add(unknownClean);

            int count = 0;
            for (Item item : BuiltInRegistries.ITEM) {
                ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
                if (!Config.isSlurryTarget(itemId)) {
                    continue;
                }
                // ワールドに依存しない色（ソルト 0）で見分けだけ付ける
                int tint = OriginHelper.tintColor(OriginHelper.computePower(item, 0L));
                EverythingSlurry dirty = new EverythingSlurry(ChemicalBuilder.dirtySlurry().tint(tint), item, true);
                EverythingSlurry clean = new EverythingSlurry(ChemicalBuilder.cleanSlurry().tint(tint), item, false);
                helper.register(slurryId("dirty", itemId), dirty);
                helper.register(slurryId("clean", itemId), clean);
                DIRTY_BY_ITEM.put(item, dirty);
                CLEAN_BY_ITEM.put(item, clean);
                ALL.add(dirty);
                ALL.add(clean);
                count++;
            }
            MekanismEverythingOreProcessing.LOGGER.info("Registered Everything Slurry pairs for {} items", count);
        });
    }

    private static ResourceLocation slurryId(String prefix, ResourceLocation itemId) {
        return MekanismEverythingOreProcessing.rl(prefix + "/" + itemId.getNamespace() + "/" + itemId.getPath());
    }

    // ------------------------------------------------------------ 参照

    /** アイテムに対応する Dirty スラリー。無ければ汎用。 */
    public static Chemical dirtyFor(@Nullable Item item) {
        EverythingSlurry slurry = item == null ? null : DIRTY_BY_ITEM.get(item);
        return slurry == null ? unknownDirty : slurry;
    }

    /** Dirty スラリーに対応する Clean スラリー。対応が無ければ汎用。 */
    public static Chemical cleanForDirty(Chemical dirty) {
        if (dirty instanceof EverythingSlurry slurry && slurry.getSourceItem() != null) {
            EverythingSlurry clean = CLEAN_BY_ITEM.get(slurry.getSourceItem());
            if (clean != null) {
                return clean;
            }
        }
        return unknownClean;
    }

    /** スラリー（Dirty / Clean どちらでも）の元アイテム。汎用や他 mod の化学物質なら null。 */
    @Nullable
    public static Item sourceItemOf(Chemical chemical) {
        if (chemical instanceof EverythingSlurry slurry) {
            return slurry.getSourceItem();
        }
        return null;
    }

    /** Chemical を Holder に包む（ChemicalStack のコンストラクタが Holder を要求するため）。 */
    public static Holder<Chemical> holderOf(Chemical chemical) {
        return MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical);
    }

    /** 登録した全スラリー（実行時データパックのタグ生成用）。 */
    public static List<EverythingSlurry> all() {
        return Collections.unmodifiableList(ALL);
    }
}
