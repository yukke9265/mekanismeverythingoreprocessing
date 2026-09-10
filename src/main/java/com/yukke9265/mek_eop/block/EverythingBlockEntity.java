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
 * 縺ｪ繧薙〒繧ゅヶ繝ｭ繝・け縺ｮ BlockEntity縲・
 * <p>
 * 蠖ｹ蜑ｲ縺ｯ縲瑚ｨｭ鄂ｮ縺輔ｌ縺溘ヶ繝ｭ繝・け縺悟・繧｢繧､繝・Β諠・ｱ繧貞ｿ倥ｌ縺ｪ縺・ｈ縺・↓縺吶ｋ縲阪□縺代・
 * 險ｭ鄂ｮ譎ゅ・ BlockItem 縺ｮ Component 縺九ｉ蜿励￠蜿悶ｊ・・pplyImplicitComponents・峨・
 * 遐ｴ螢頑凾縺ｯ loot table 縺ｮ copy_components 縺ｧ Component 縺ｸ謌ｻ縺呻ｼ・ollectImplicitComponents・峨・
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

    // ------------------------------------------------------------ Component 縺ｨ縺ｮ蜿励￠貂｡縺・

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

    // ------------------------------------------------------------ NBT 菫晏ｭ・

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

    // ------------------------------------------------------------ 繧ｯ繝ｩ繧､繧｢繝ｳ繝亥酔譛滂ｼ医ユ繧｣繝ｳ繝郁牡逕ｨ・・

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
