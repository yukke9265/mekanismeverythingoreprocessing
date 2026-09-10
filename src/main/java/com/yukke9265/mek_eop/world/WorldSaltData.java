package com.yukke9265.mek_eop.world;

import java.util.random.RandomGenerator;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.saveddata.SavedData;

/**
 * ワールド固有のソルト。
 * <p>
 * power（元アイテムのハッシュ）をワールドごとに違う値にするために使う。
 * 初回アクセス時にランダム生成し、以後はそのワールドで固定される。
 * Overworld の DataStorage に保存するので、全ディメンションで同じ値になる。
 */
public class WorldSaltData extends SavedData {

    private static final String FILE_NAME = "mek_eop_world_salt";
    private static final String TAG_SALT = "salt";

    private static final Factory<WorldSaltData> FACTORY = new Factory<>(WorldSaltData::createNew, WorldSaltData::load, null);

    private final long salt;

    private WorldSaltData(long salt) {
        this.salt = salt;
    }

    private static WorldSaltData createNew() {
        WorldSaltData data = new WorldSaltData(RandomGenerator.getDefault().nextLong());
        // 生成直後に保存対象へ入れる
        data.setDirty();
        return data;
    }

    private static WorldSaltData load(CompoundTag tag, HolderLookup.Provider provider) {
        return new WorldSaltData(tag.getLong(TAG_SALT));
    }

    @Override
    public CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putLong(TAG_SALT, salt);
        return tag;
    }

    /** サーバーからソルトを取り出す（無ければ生成）。 */
    public static long getSalt(MinecraftServer server) {
        WorldSaltData data = server.overworld().getDataStorage().computeIfAbsent(FACTORY, FILE_NAME);
        return data.salt;
    }
}
