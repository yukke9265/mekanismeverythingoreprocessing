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
 * 螳溯｡梧凾縺ｫ荳ｭ霄ｫ繧剃ｽ懊ｋ繝・・繧ｿ繝代ャ繧ｯ縲・
 * <p>
 * 逶ｮ逧・ 蜍慕噪縺ｫ逋ｻ骭ｲ縺励◆縺ｪ繧薙〒繧ゅせ繝ｩ繝ｪ繝ｼ繧貞喧蟄ｦ迚ｩ雉ｪ繧ｿ繧ｰ縺ｫ蜈･繧後ｋ縲・
 * 繧ｿ繧ｰ縺ｯ繝・・繧ｿ繝代ャ繧ｯ逕ｱ譚･縺ｪ縺ｮ縺ｧ縲［od 縺ｮ jar 縺ｫ髱咏噪縺ｫ鄂ｮ縺上％縺ｨ縺後〒縺阪↑縺・ｼ医い繧､繝・Β讒区・縺檎腸蠅・＃縺ｨ縺ｫ驕輔≧・峨・
 * <p>
 * 逕滓・縺吶ｋ繧ゅ・:
 * <ul>
 * <li>mekanismeverythingoreprocessing:dirty_everything_slurry / clean_everything_slurry・医Ξ繧ｷ繝斐・蜈･蜉帷畑・・/li>
 * <li>mekanism:dirty / mekanism:clean・・ekanism 縺ｮ陦ｨ遉ｺ逕ｨ蛻・｡槭↓霑ｽ蜉・・/li>
 * <li>c:hidden_from_recipe_viewers・・EI 縺ｨ繧ｯ繝ｪ繧ｨ繧､繝・ぅ繝悶ち繝悶°繧蛾國縺呻ｼ・/li>
 * </ul>
 */
public class EverythingRuntimePack extends AbstractPackResources {

    public static final String PACK_ID = MekanismEverythingOreProcessing.MODID + "_runtime";

    /** 1.21.1 縺ｮ繝・・繧ｿ繝代ャ繧ｯ蠖｢蠑上・*/
    private static final int PACK_FORMAT = 48;

    /** 逕滓・貂医∩繝輔ぃ繧､繝ｫ縲ゅく繝ｼ縺ｯ "data/..." 縺九ｉ蟋九∪繧狗嶌蟇ｾ繝代せ縲・*/
    private final Map<ResourceLocation, byte[]> files = new LinkedHashMap<>();

    public EverythingRuntimePack(PackLocationInfo location) {
        super(location);
        buildFiles();
    }

    // ------------------------------------------------------------ 荳ｭ霄ｫ縺ｮ逕滓・

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

    // ------------------------------------------------------------ PackResources 螳溯｣・

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
        // 繝｡繝｢繝ｪ荳翫・繝・・繧ｿ縺縺代↑縺ｮ縺ｧ髢峨§繧九ｂ縺ｮ縺ｯ辟｡縺・
    }

    private static IoSupplier<InputStream> supplierOf(byte[] data) {
        return () -> new ByteArrayInputStream(data);
    }
}
