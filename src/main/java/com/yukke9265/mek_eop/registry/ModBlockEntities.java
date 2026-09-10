package com.yukke9265.mek_eop.registry;

import com.yukke9265.mek_eop.MekanismEverythingOreProcessing;
import com.yukke9265.mek_eop.block.EverythingBlockEntity;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

/** BlockEntity の登録。 */
public final class ModBlockEntities {
    private ModBlockEntities() {
    }

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, MekanismEverythingOreProcessing.MODID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<EverythingBlockEntity>> EVERYTHING_BLOCK =
            BLOCK_ENTITIES.register("everything_block",
                    () -> BlockEntityType.Builder.of(EverythingBlockEntity::new, ModBlocks.EVERYTHING_BLOCK.get()).build(null));

    public static void register(IEventBus modEventBus) {
        BLOCK_ENTITIES.register(modEventBus);
    }
}
