package com.bergerkiller.generated.net.minecraft.world.level.chunk;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.HeightMap;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.EnumSkyBlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import org.bukkit.Chunk;
import org.bukkit.entity.Entity;
import java.util.Collection;
import java.util.List;

/**
 * Instance wrapper handle for type <b>net.minecraft.world.level.chunk.Chunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.world.level.chunk.Chunk")
public abstract class ChunkHandle extends Template.Handle {
    /** @see ChunkClass */
    public static final ChunkClass T = Template.Class.create(ChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract WorldHandle getWorld();
    public abstract int getLocX();
    public abstract int getLocZ();
    public abstract List<Integer> getLoadedSectionCoordinates();
    public abstract ChunkSection[] getSections();
    public abstract Object getSectionRaw(int cy);
    public abstract ChunkSection getSection(int cy);
    public abstract Collection<?> getRawTileEntities();
    public abstract List<Entity> getEntities();
    public abstract Chunk getBukkitChunk();
    public abstract BlockData getBlockData(IntVector3 blockposition);
    public abstract BlockData getBlockDataAtCoord(int x, int y, int z);
    public abstract BlockData setBlockData(IntVector3 blockposition, BlockData iblockdata, int updateFlags);
    public abstract void addEntity(EntityHandle entity);
    public abstract HeightMap getLightHeightMap(boolean initialize);
    public abstract int getBrightness(EnumSkyBlockHandle enumskyblock, IntVector3 position);
    public abstract int getTopSliceY();
    public abstract void addEntities();
    public abstract boolean checkCanSave(boolean isNotAutosave);
    public abstract void markDirty();
    public abstract void markEntitiesDirty();
    public java.util.Collection<org.bukkit.block.BlockState> getTileEntities() {
        java.util.Collection<?> tileEntities = getRawTileEntities();
        com.bergerkiller.bukkit.common.conversion.blockstate.ChunkBlockStateConverter chunkBlockStateConverter;
        chunkBlockStateConverter = new com.bergerkiller.bukkit.common.conversion.blockstate.ChunkBlockStateConverter(getBukkitChunk());
        return new com.bergerkiller.mountiplex.conversion.util.ConvertingCollection(tileEntities, chunkBlockStateConverter);
    }

    public static ChunkHandle fromBukkit(org.bukkit.Chunk chunk) {
        if (chunk != null) {
            return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toChunkHandle(chunk));
        } else {
            return null;
        }
    }
    /**
     * Stores class members for <b>net.minecraft.world.level.chunk.Chunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ChunkClass extends Template.Class<ChunkHandle> {
        public final Template.Method.Converted<WorldHandle> getWorld = new Template.Method.Converted<WorldHandle>();
        public final Template.Method<Integer> getLocX = new Template.Method<Integer>();
        public final Template.Method<Integer> getLocZ = new Template.Method<Integer>();
        public final Template.Method<List<Integer>> getLoadedSectionCoordinates = new Template.Method<List<Integer>>();
        public final Template.Method<ChunkSection[]> getSections = new Template.Method<ChunkSection[]>();
        public final Template.Method<Object> getSectionRaw = new Template.Method<Object>();
        public final Template.Method<ChunkSection> getSection = new Template.Method<ChunkSection>();
        public final Template.Method<Collection<?>> getRawTileEntities = new Template.Method<Collection<?>>();
        public final Template.Method.Converted<List<Entity>> getEntities = new Template.Method.Converted<List<Entity>>();
        public final Template.Method<Chunk> getBukkitChunk = new Template.Method<Chunk>();
        public final Template.Method.Converted<BlockData> getBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> getBlockDataAtCoord = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<BlockData> setBlockData = new Template.Method.Converted<BlockData>();
        public final Template.Method.Converted<Void> addEntity = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<HeightMap> getLightHeightMap = new Template.Method.Converted<HeightMap>();
        public final Template.Method.Converted<Integer> getBrightness = new Template.Method.Converted<Integer>();
        public final Template.Method<Integer> getTopSliceY = new Template.Method<Integer>();
        public final Template.Method<Void> addEntities = new Template.Method<Void>();
        public final Template.Method<Boolean> checkCanSave = new Template.Method<Boolean>();
        public final Template.Method<Void> markDirty = new Template.Method<Void>();
        public final Template.Method<Void> markEntitiesDirty = new Template.Method<Void>();

    }

}

