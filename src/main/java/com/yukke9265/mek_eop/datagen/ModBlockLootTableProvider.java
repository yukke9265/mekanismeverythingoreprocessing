package com.yukke9265.mek_eop.datagen;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import com.yukke9265.mek_eop.registry.ModBlocks;
import com.yukke9265.mek_eop.registry.ModDataComponents;

import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;

/** ブロックのルートテーブル。なんでもブロックは BlockEntity の元情報を Component にコピーしてドロップする。 */
public class ModBlockLootTableProvider extends LootTableProvider {

    public ModBlockLootTableProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(new SubProviderEntry(BlockLoot::new, LootContextParamSets.BLOCK)), registries);
    }

    private static class BlockLoot extends BlockLootSubProvider {

        protected BlockLoot(HolderLookup.Provider registries) {
            super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
        }

        @Override
        protected void generate() {
            Block block = ModBlocks.EVERYTHING_BLOCK.get();
            add(block, LootTable.lootTable().withPool(applyExplosionCondition(block, LootPool.lootPool()
                    .setRolls(ConstantValue.exactly(1.0f))
                    .add(LootItem.lootTableItem(block)
                            .apply(CopyComponentsFunction.copyComponents(CopyComponentsFunction.Source.BLOCK_ENTITY)
                                    .include(ModDataComponents.ORIGINAL_ITEM.get()))))));
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return List.of(ModBlocks.EVERYTHING_BLOCK.get());
        }
    }
}
