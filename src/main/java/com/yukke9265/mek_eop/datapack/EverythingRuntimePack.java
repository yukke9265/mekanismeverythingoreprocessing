package com.yukke9265.mek_eop.datapack;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.jetbrains.annotations.Nullable;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.chemical.EverythingSlurries;
import com.yukke9265.mek_eop.chemical.EverythingSlurry;
import com.yukke9265.mek_eop.registry.ModTags;

import mekanism.api.MekanismAPI;
import mekanism.api.MekanismAPITags;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.AbstractPackResources;
import net.minecraft.server.packs.PackLocationInfo;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.tags.TagKey;

/**
 * 実行時に中身を作るデータパック。
 * <p>
 * 目的: 動的に登録したなんでもスラリーを化学物質タグに入れる。
 * タグはデータパック経由なので、Mod の jar に静的に置けない（アイテム数ごとに違う）。
 * <p>
 * 生成するもの:
 * <ul>
 * <li>mekanismeverythingoreprocessing:dirty_everything_slurry / clean_everything_slurry（レシピの入力用）</li>
 * <li>mekanism:dirty / mekanism:clean（Mekanism の表示用分類に追加）</li>
 * <li>c:hidden_from_recipe_viewers（JEI とクリエイティブタブから隠す）</li>
 * </ul>
 */
public class EverythingRuntimePack extends AbstractPackResources {

    public static final String PACK_ID = MekanismEverythingOreProcessing.MODID + "_runtime";

    /** 1.21.1 のデータパック形式。 */
    private static final int PACK_FORMAT = 48;

    /** 生成済みファイル。キーは "data/..." から始まる相対パス。 */
    private final Map<ResourceLocation, byte[]> files = new LinkedHashMap<>();

    public EverythingRuntimePack(PackLocationInfo location) {
        super(location);
        buildFiles();
    }

    // ------------------------------------------------------------ 中身の生成

    private void buildFiles() {
        JsonArray dirtyIds = new JsonArray();
        JsonArray cleanIds = new JsonArray();
        JsonArray allIds = new JsonArray();
        for (EverythingSlurry slurry : EverythingSlurries.all()) {
            ResourceLocation id = MekanismAPI.CHEMICAL_REGISTRY.getKey(slurry);
            if (id == null) {
                continue;
            }
            allIds.add(id.toString());
            if (slurry.isDirty()) {
                dirtyIds.add(id.toString());
            } else {
                cleanIds.add(id.toString());
            }
        }

        putTag(ModTags.Chemicals.DIRTY_EVERYTHING_SLURRY, dirtyIds);
        putTag(ModTags.Chemicals.CLEAN_EVERYTHING_SLURRY, cleanIds);
        putTag(MekanismAPITags.Chemicals.DIRTY, dirtyIds);
        putTag(MekanismAPITags.Chemicals.CLEAN, cleanIds);
        putTag(MekanismAPITags.Chemicals.HIDDEN_FROM_RECIPE_VIEWERS, allIds);
    }

    private void putTag(TagKey<Chemical> tag, JsonArray values) {
        JsonObject json = new JsonObject();
        json.addProperty("replace", false);
        json.add("values", values);
        // data/<ns>/tags/mekanism/chemical/<path>.json
        String path = Registries.tagsDirPath(MekanismAPI.CHEMICAL_REGISTRY_NAME) + "/" + tag.location().getPath() + ".json";
        ResourceLocation file = ResourceLocation.fromNamespaceAndPath(tag.location().getNamespace(), path);
        files.put(file, json.toString().getBytes(StandardCharsets.UTF_8));
    }

    // ------------------------------------------------------------ PackResources 実装

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        if (elements.length == 1 && elements[0].equals("pack.mcmeta")) {
            String meta = "{\"pack\":{\"description\":\"Everything Slurry tags\",\"pack_format\":" + PACK_FORMAT + "}}";
            return supplierOf(meta.getBytes(StandardCharsets.UTF_8));
        }
        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType type, ResourceLocation location) {
        if (type != PackType.SERVER_DATA) {
            return null;
        }
        byte[] data = files.get(location);
        return data == null ? null : supplierOf(data);
    }

    @Override
    public void listResources(PackType type, String namespace, String pathPrefix, ResourceOutput output) {
        if (type != PackType.SERVER_DATA) {
            return;
        }
        for (Map.Entry<ResourceLocation, byte[]> entry : files.entrySet()) {
            ResourceLocation location = entry.getKey();
            if (location.getNamespace().equals(namespace) && location.getPath().startsWith(pathPrefix)) {
                output.accept(location, supplierOf(entry.getValue()));
            }
        }
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        if (type != PackType.SERVER_DATA) {
            return Set.of();
        }
        Set<String> namespaces = new HashSet<>();
        for (ResourceLocation location : files.keySet()) {
            namespaces.add(location.getNamespace());
        }
        return namespaces;
    }

    @Override
    public void close() {
        // メモリ上のデータだけなので閉じるものはない
    }

    private static IoSupplier<InputStream> supplierOf(byte[] data) {
        return () -> new ByteArrayInputStream(data);
    }
}
