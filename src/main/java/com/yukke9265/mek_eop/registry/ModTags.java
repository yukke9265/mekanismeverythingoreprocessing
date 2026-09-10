package com.yukke9265.mek_eop.registry;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

/** この mod が使うタグ。 */
public final class ModTags {
    private ModTags() {
    }

    public static final class Items {
        private Items() {
        }

        /** 変換禁止アイテム。 */
        public static final TagKey<Item> BLACKLIST = ItemTags.create(MekanismEverythingOreProcessing.rl("blacklist"));

        /** 元情報なしのなんでも原石を作る素材（c:raw_materials/osmium）。 */
        public static final TagKey<Item> RAW_OSMIUM = TagKey.create(Registries.ITEM,
                ResourceLocation.fromNamespaceAndPath("c", "raw_materials/osmium"));
    }

    public static final class Chemicals {
        private Chemicals() {
        }

        /** 全てのなんでもダーティスラリー（実行時データパックで中身を生成）。 */
        public static final TagKey<Chemical> DIRTY_EVERYTHING_SLURRY = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME,
                MekanismEverythingOreProcessing.rl("dirty_everything_slurry"));

        /** 全てのなんでもクリーンスラリー（実行時データパックで中身を生成）。 */
        public static final TagKey<Chemical> CLEAN_EVERYTHING_SLURRY = TagKey.create(MekanismAPI.CHEMICAL_REGISTRY_NAME,
                MekanismEverythingOreProcessing.rl("clean_everything_slurry"));
    }
}
