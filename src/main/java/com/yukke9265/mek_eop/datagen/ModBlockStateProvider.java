package com.yukke9265.mek_eop.datagen;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.registry.ModBlocks;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

/** blockstate / block model の生成。 */
public class ModBlockStateProvider extends BlockStateProvider {

    public ModBlockStateProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, MekanismEverythingOreProcessing.MODID, existingFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        // cube_all だと tintindex が無いので、全面 tintindex=0 の立方体モデルを自前で組む
        ResourceLocation texture = modLoc("block/everything_block");
        ModelFile model = models().withExistingParent("everything_block", mcLoc("block/block"))
                .texture("all", texture)
                .texture("particle", texture)
                .element()
                .from(0, 0, 0).to(16, 16, 16)
                .allFaces((direction, face) -> face.texture("#all").cullface(direction).tintindex(0))
                .end();
        simpleBlock(ModBlocks.EVERYTHING_BLOCK.get(), model);
    }
}
