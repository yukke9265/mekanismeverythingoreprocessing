package com.yukke9265.mek_eop.block;

import org.jetbrains.annotations.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.serialization.MapCodec;

/**
 * なんでもブロック（なんでもインゴット ×9 の保管ブロック）。
 * <p>
 * 見た目は普通のブロックだが、元アイテム情報を保持するために BlockEntity を持つ。
 */
public class EverythingBlock extends BaseEntityBlock {

    public static final MapCodec<EverythingBlock> CODEC = simpleCodec(EverythingBlock::new);

    public EverythingBlock(Properties properties) {
        super(properties);
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    protected RenderShape getRenderShape(BlockState state) {
        // BaseEntityBlock は既定で見えなくなるので、通常モデル描画に戻す
        return RenderShape.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EverythingBlockEntity(pos, state);
    }
}
