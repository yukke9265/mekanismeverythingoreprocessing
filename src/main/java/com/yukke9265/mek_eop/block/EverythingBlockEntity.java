package com.yukke9265.mek_eop.block;

import org.jetbrains.annotations.Nullable;

import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModBlockEntities;
import com.yukke9265.mek_eop.registry.ModDataComponents;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

/**
 * なんでもブロックの BlockEntity。
 * <p>
 * 役割は「設置されたブロックが元アイテム情報を忘れないようにする」だけ。
 * 設置時は BlockItem の Component から受け取り（applyImplicitComponents）、
 * 破壊時は loot table の copy_components で Component へ返す（collectImplicitComponents）。
 */
public class EverythingBlockEntity extends BlockEntity {

    private static final String TAG_ORIGIN = "origin";

    @Nullable
    private OriginalItem origin;

    public EverythingBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.EVERYTHING_BLOCK.get(), pos, state);
    }

    @Nullable
    public OriginalItem getOrigin() {
        return origin;
    }

    // ------------------------------------------------------------ Component との受け渡し

    @Override
    protected void applyImplicitComponents(DataComponentInput input) {
        super.applyImplicitComponents(input);
        origin = input.get(ModDataComponents.ORIGINAL_ITEM.get());
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder builder) {
        super.collectImplicitComponents(builder);
        if (origin != null) {
            builder.set(ModDataComponents.ORIGINAL_ITEM.get(), origin);
        }
    }

    @Override
    public void removeComponentsFromTag(CompoundTag tag) {
        super.removeComponentsFromTag(tag);
        tag.remove(TAG_ORIGIN);
    }

    // ------------------------------------------------------------ NBT 保存

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        if (origin != null) {
            Tag encoded = OriginalItem.CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), origin)
                    .result().orElse(null);
            if (encoded != null) {
                tag.put(TAG_ORIGIN, encoded);
            }
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        origin = null;
        if (tag.contains(TAG_ORIGIN)) {
            origin = OriginalItem.CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag.get(TAG_ORIGIN))
                    .result().orElse(null);
        }
    }

    // ------------------------------------------------------------ クライアント同期（ティント用）

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithoutMetadata(registries);
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
