package com.yukke9265.mek_eop.recipe.crafting;

import java.util.Objects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.yukke9265.mek_eop.component.OriginalItem;
import com.yukke9265.mek_eop.registry.ModRecipeSerializers;
import com.yukke9265.mek_eop.util.OriginHelper;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

/**
 * 蜈・ュ蝣ｱ繧剃ｿ昴▲縺溘∪縺ｾ蛟区焚繧定ｩｰ繧∵崛縺医ｋ繧ｯ繝ｩ繝輔ヨ・医リ繧ｲ繝・ヨ 竍・繧､繝ｳ繧ｴ繝・ヨ縲√う繝ｳ繧ｴ繝・ヨ 竍・繝悶Ο繝・け・峨・
 * <p>
 * from 繧・fromCount 蛟具ｼ亥・縺ｦ蜷後§蜈・ュ蝣ｱ・我ｸｦ縺ｹ繧九→ to 縺・toCount 蛟九〒縺阪ｋ縲・
 * 菴咲ｽｮ縺ｯ蝠上ｏ縺ｪ縺・ｼ井ｸ榊ｮ壼ｽ｢・峨ょ・諠・ｱ縺碁＆縺・い繧､繝・Β縺梧ｷｷ縺悶▲縺ｦ縺・ｋ縺ｨ荳閾ｴ縺励↑縺・・
 */
public class OriginPackRecipe implements CraftingRecipe {

    public static final MapCodec<OriginPackRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("from").forGetter(recipe -> recipe.from),
            ExtraCodecs.POSITIVE_INT.fieldOf("from_count").forGetter(recipe -> recipe.fromCount),
            BuiltInRegistries.ITEM.holderByNameCodec().fieldOf("to").forGetter(recipe -> recipe.to),
            ExtraCodecs.POSITIVE_INT.fieldOf("to_count").forGetter(recipe -> recipe.toCount)
    ).apply(instance, OriginPackRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, OriginPackRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.holderRegistry(Registries.ITEM), recipe -> recipe.from,
            ByteBufCodecs.VAR_INT, recipe -> recipe.fromCount,
            ByteBufCodecs.holderRegistry(Registries.ITEM), recipe -> recipe.to,
            ByteBufCodecs.VAR_INT, recipe -> recipe.toCount,
            OriginPackRecipe::new
    );

    private final Holder<Item> from;
    private final int fromCount;
    private final Holder<Item> to;
    private final int toCount;

    public OriginPackRecipe(Holder<Item> from, int fromCount, Holder<Item> to, int toCount) {
        this.from = from;
        this.fromCount = fromCount;
        this.to = to;
        this.toCount = toCount;
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        int found = 0;
        OriginalItem sharedOrigin = null;
        boolean first = true;
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!stack.is(from)) {
                return false;
            }
            OriginalItem origin = OriginHelper.getOrigin(stack);
            if (first) {
                sharedOrigin = origin;
                first = false;
            } else if (!Objects.equals(sharedOrigin, origin)) {
                // 蜈・ュ蝣ｱ縺梧ｷｷ縺悶▲縺ｦ縺・ｋ
                return false;
            }
            found++;
        }
        return found == fromCount;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack result = new ItemStack(to, toCount);
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                OriginHelper.copyOrigin(stack, result);
                break;
            }
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= fromCount;
    }

    @Override
    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(to, toCount);
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // 繝ｬ繧ｷ繝斐ヶ繝・け / JEI 陦ｨ遉ｺ逕ｨ縲ょｮ滄圀縺ｮ蛻､螳壹・ matches 縺ｧ陦後≧
        NonNullList<Ingredient> list = NonNullList.create();
        for (int i = 0; i < fromCount; i++) {
            list.add(Ingredient.of(from.value()));
        }
        return list;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.ORIGIN_PACK.get();
    }
}
