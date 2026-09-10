package com.yukke9265.mek_eop.datagen;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModItems;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelBuilder;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.registries.DeferredItem;

/**
 * item model の生成。
 * <p>
 * なんでも○○は 2 つのモデルを持つ:
 * <ul>
 * <li>{@code everything_x_base}: Mekanism 流の 2 層モデル（layer0 = ティント対象のベース、layer1 = ティント無しのオーバーレイ）</li>
 * <li>{@code everything_x}: builtin/entity。描画は EverythingItemRenderer に任せ、そこで base モデル + 元アイテムの小アイコンを描く</li>
 * </ul>
 */
public class ModItemModelProvider extends ItemModelProvider {

    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MekanismEverythingOreProcessing.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        everythingItem(ModItems.EVERYTHING_RAW_ORE, false);
        everythingItem(ModItems.EVERYTHING_DUST, false);
        everythingItem(ModItems.EVERYTHING_DIRTY_DUST, true);
        everythingItem(ModItems.EVERYTHING_CLUMP, true);
        everythingItem(ModItems.EVERYTHING_SHARD, true);
        everythingItem(ModItems.EVERYTHING_CRYSTAL, true);
        everythingItem(ModItems.EVERYTHING_INGOT, false);
        withExistingParent("everything_block", modLoc("block/everything_block"));
    }

    private void everythingItem(DeferredItem<? extends Item> item, boolean hasOverlay) {
        String name = item.getId().getPath();

        // 実際の見た目（Mekanism と同じ層構成）
        ItemModelBuilder base = withExistingParent(ModItems.baseModelPath(name), mcLoc("item/generated"))
                .texture("layer0", modLoc("item/" + name));
        if (hasOverlay) {
            base.texture("layer1", modLoc("item/" + name + "_overlay"));
        }

        // 登録名のモデルは描画を BEWLR に任せる。gui_light=front で平面アイテムと同じ明るさにする
        getBuilder(name)
                .parent(new ModelFile.UncheckedModelFile(ResourceLocation.withDefaultNamespace("builtin/entity")))
                .guiLight(BlockModel.GuiLight.FRONT);
    }
}
