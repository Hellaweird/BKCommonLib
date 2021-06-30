package com.bergerkiller.bukkit.common.internal.logic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Chunk;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonPlugin;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.utils.WorldUtil;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryHandle;
import com.bergerkiller.generated.net.minecraft.server.level.EntityTrackerEntryStateHandle;
import com.bergerkiller.generated.net.minecraft.server.level.PlayerChunkHandle;
import com.bergerkiller.generated.net.minecraft.server.level.PlayerChunkMapHandle;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.util.IntHashMapHandle;
import com.bergerkiller.generated.net.minecraft.world.entity.EntityHandle;
import com.bergerkiller.generated.net.minecraft.world.level.WorldHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.resolver.Resolver;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.mountiplex.reflection.util.asm.MPLType;

/**
 * From Minecraft 1.14 onwards the best way to listen to entity add/remove events is
 * to hook the 'entitiesByUUID' map, and override the methods that add/remove from it.
 */
public class EntityAddRemoveHandler_1_14_to_1_16_5 extends EntityAddRemoveHandler {
    private final FastField<?> entitiesByIdField = new FastField<Object>();
    private final FastField<Map<UUID, Object>> entitiesByUUIDField = new FastField<Map<UUID, Object>>();
    private final SafeField<Queue<Object>> entitiesToAddField;
    private final List<EntitiesByUUIDMapHook> hooks = new ArrayList<EntitiesByUUIDMapHook>();
    private final FastMethod<Object> tuinitySwapEntityInWorldEntityListMethod = new FastMethod<Object>();
    private final FastMethod<Object> tuinitySwapEntityInWorldEntityIterationSetMethod = new FastMethod<Object>();
    private final ChunkEntitySliceHandler chunkEntitySliceHandler;

    public EntityAddRemoveHandler_1_14_to_1_16_5() {
        //Field 'entitiesById' in class net.minecraft.server.v1_15_R1.WorldServer is of type Int2ObjectLinkedOpenHashMap while we expect type Int2ObjectMap
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entitiesById");
            entitiesByIdField.init(MPLType.getDeclaredField(WorldServerHandle.T.getType(), fieldName));
            if (!IntHashMapHandle.T.isAssignableFrom(entitiesByIdField.getType())) {
                throw new IllegalStateException("Field not assignable to IntHashmap");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize WorldServer entitiesById field: " + t.getMessage(), t);
            entitiesByIdField.initUnavailable("entitiesById");
        }

        // EntitiesByUUID is a Map on MC 1.16.5 and before
        try {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entitiesByUUID");
            entitiesByUUIDField.init(MPLType.getDeclaredField(WorldServerHandle.T.getType(), fieldName));
            if (!Map.class.isAssignableFrom(entitiesByUUIDField.getType())) {
                throw new IllegalStateException("Field not assignable to Map");
            }
        } catch (Throwable t) {
            Logging.LOGGER_REFLECTION.log(Level.WARNING, "Failed to initialize WorldServer entitiesByUUID field: " + t.getMessage(), t);
            entitiesByUUIDField.initUnavailable("entitiesByUUID");
        }

        {
            String fieldName = Resolver.resolveFieldName(WorldServerHandle.T.getType(), "entitiesToAdd");
            this.entitiesToAddField = CommonUtil.unsafeCast(SafeField.create(WorldServerHandle.T.getType(), fieldName, Queue.class));
        }

        // Tuinity support: 'loadedEntities' field of WorldServer
        try {
            Class<?> entityListType = Class.forName("com.tuinity.tuinity.util.EntityList");
            if (SafeField.contains(WorldServerHandle.T.getType(), "loadedEntities", entityListType)) {
                ClassResolver resolver = new ClassResolver();
                resolver.addImport("net.minecraft.world.entity.Entity");
                resolver.setDeclaredClassName("net.minecraft.server.level.WorldServer");
                tuinitySwapEntityInWorldEntityListMethod.init(new MethodDeclaration(resolver,
                        "public void swap(Entity oldEntity, Entity newEntity) {\n" +
                        "    if (instance.loadedEntities.remove(oldEntity)) {\n" +
                        "        instance.loadedEntities.add(newEntity);\n" +
                        "    }\n" +
                        "}"
                ));
            }
        } catch (ClassNotFoundException ignore) {}

        // Tuinity support: 'entitiesForIteration' field of WorldServer
        try {
            Class<?> entitySetType = Class.forName("com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet");
            if (SafeField.contains(WorldServerHandle.T.getType(), "entitiesForIteration", entitySetType)) {
                ClassResolver resolver = new ClassResolver();
                resolver.addImport("net.minecraft.world.entity.Entity");
                resolver.setDeclaredClassName("net.minecraft.server.level.WorldServer");
                tuinitySwapEntityInWorldEntityIterationSetMethod.init(new MethodDeclaration(resolver,
                        "public void swap(Entity oldEntity, Entity newEntity) {\n" +
                        "    #require net.minecraft.server.level.WorldServer final com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet<net.minecraft.world.entity.Entity> entitiesForIteration;\n" +
                        "    com.tuinity.tuinity.util.maplist.IteratorSafeOrderedReferenceSet set = instance#entitiesForIteration;\n" +
                        "    if (set.remove(oldEntity)) {\n" +
                        "        set.add(newEntity);\n" +
                        "    }\n" +
                        "}"
                ));
            }
        } catch (ClassNotFoundException ignore) {}

        // Chunk EntitySlice[] field, used when entities need to be swapped, or when removing
        // entities from a chunk/world
        this.chunkEntitySliceHandler = new ChunkEntitySliceHandler();
    }

    @Override
    public void processEvents() {
        for (EntitiesByUUIDMapHook hook : hooks) {
            hook.processEvents();
        }
    }

    @Override
    protected void hook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> base = this.entitiesByUUIDField.get(nmsWorldHandle);
        if (!(base instanceof EntitiesByUUIDMapHook)) {
            EntitiesByUUIDMapHook hook = new EntitiesByUUIDMapHook(this, world, base);
            this.entitiesByUUIDField.set(nmsWorldHandle, hook);
            hooks.add(hook);
        }
    }

    @Override
    protected void unhook(World world) {
        Object nmsWorldHandle = WorldHandle.fromBukkit(world).getRaw();
        Map<UUID, Object> value = this.entitiesByUUIDField.get(nmsWorldHandle);
        if (value instanceof EntitiesByUUIDMapHook) {
            this.entitiesByUUIDField.set(nmsWorldHandle, ((EntitiesByUUIDMapHook) value).getBase());
            hooks.remove(value);
        }
    }

    @Override
    public void replace(World world, EntityHandle oldEntity, EntityHandle newEntity) {
        Object worldHandle = oldEntity.getWorld().getRaw();

        // *** Remove from the entities to add queue ***
        Queue<Object> entitiesToAdd = this.entitiesToAddField.get(oldEntity.getWorld().getRaw());
        entitiesToAdd.remove(oldEntity.getRaw());

        // *** Entities By UUID Map ***
        {
            Map<UUID, Object> entitiesByUUID = this.entitiesByUUIDField.get(worldHandle);
            Object storedEntityHandle = entitiesByUUID.get(oldEntity.getUniqueID());
            if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
                if (!oldEntity.getUniqueID().equals(newEntity.getUniqueID())) {
                    entitiesByUUID.remove(oldEntity.getUniqueID());
                }
                entitiesByUUID.put(newEntity.getUniqueID(), newEntity.getRaw());
            }
        }

        // *** Entities by Id Map ***
        {
            IntHashMapHandle entitiesById = IntHashMapHandle.createHandle(this.entitiesByIdField.get(worldHandle));
            Object storedEntityHandle = entitiesById.get(oldEntity.getIdField());
            if (storedEntityHandle != null && storedEntityHandle != newEntity.getRaw()) {
                if (oldEntity.getIdField() != newEntity.getIdField()) {
                    entitiesById.remove(oldEntity.getIdField());
                }
                entitiesById.put(newEntity.getIdField(), newEntity.getRaw());
            }
        }

        // *** Tuinity WorldServer EntityList field ***
        if (tuinitySwapEntityInWorldEntityListMethod.isAvailable()) {
            tuinitySwapEntityInWorldEntityListMethod.invoke(worldHandle, oldEntity.getRaw(), newEntity.getRaw());
        }

        // *** Tuinity WorldServer entitiesForIteration field ***
        if (tuinitySwapEntityInWorldEntityIterationSetMethod.isAvailable()) {
            tuinitySwapEntityInWorldEntityIterationSetMethod.invoke(worldHandle, oldEntity.getRaw(), newEntity.getRaw());
        }

        // *** EntityTrackerEntry ***
        replaceInEntityTracker(oldEntity, oldEntity, newEntity);
        if (oldEntity.getVehicle() != null) {
            replaceInEntityTracker(oldEntity.getVehicle(), oldEntity, newEntity);
        }
        if (oldEntity.getPassengers() != null) {
            for (EntityHandle passenger : oldEntity.getPassengers()) {
                replaceInEntityTracker(passenger, oldEntity, newEntity);
            }
        }

        // *** Entity Current Chunk ***
        final int chunkX = newEntity.getChunkX();
        final int chunkZ = newEntity.getChunkZ();
        PlayerChunkMapHandle playerChunks = WorldServerHandle.T.getPlayerChunkMap.invoke(worldHandle);
        Chunk loadedChunk = WorldUtil.getChunk(newEntity.getBukkitWorld(), chunkX, chunkZ);
        if (loadedChunk != null) {
            replaceInChunk(loadedChunk, oldEntity, newEntity);
        } else {
            // Chunk isn't loaded at this time. This gets difficult!
            // It might still be in the updating chunks mapping
            PlayerChunkHandle updatingChunk = playerChunks.getUpdatingChunk(chunkX, chunkZ);
            Chunk loadedUpdatingChunk = (updatingChunk == null) ? null : updatingChunk.getChunkIfLoaded();
            if (loadedUpdatingChunk == null && updatingChunk != null) {
                // Try hard time! This allows any status the chunk is in.
                loadedUpdatingChunk = PlayerChunkHandle.T.opt_getChunkTryHard_1_14.invoke(updatingChunk.getRaw());
            }

            // Let's go!
            replaceInChunk(loadedUpdatingChunk, oldEntity, newEntity);
        }

        // See where the object is still referenced to check we aren't missing any places to replace
        // This is SLOW, do not ever have this enabled on a release version!
        //com.bergerkiller.bukkit.common.utils.DebugUtil.logInstances(oldEntity.getRaw());
    }

    @Override
    public void moveToChunk(EntityHandle entity) {
        this.chunkEntitySliceHandler.moveToChunk(entity);
    }

    /**
     * This replaces the entitiesByUUID field in WorldServer
     * 
     * TODO: keySet(), values() and entrySet() are not hooked and removing from it
     *       is not listened to!
     */
    private static final class EntitiesByUUIDMapHook implements Map<UUID, Object> {
        private final EntityAddRemoveHandler_1_14_to_1_16_5 handler;
        private final World world;
        private final Map<UUID, Object> base;
        private final Queue<org.bukkit.entity.Entity> pendingAddEvents = new LinkedList<org.bukkit.entity.Entity>();

        public EntitiesByUUIDMapHook(EntityAddRemoveHandler_1_14_to_1_16_5 handler, World world, Map<UUID, Object> base) {
            this.handler = handler;
            this.world = world;
            this.base = base;
        }

        public Map<UUID, Object> getBase() {
            return this.base;
        }

        public void processEvents() {
            while (!pendingAddEvents.isEmpty()) {
                CommonPlugin.getInstance().notifyAdded(world, pendingAddEvents.poll());
            }
        }

        private void onAdded(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            handler.notifyAddedEarly(world, bEntity);
            pendingAddEvents.add(bEntity);
        }

        private void onRemoved(Object entity) {
            org.bukkit.entity.Entity bEntity = WrapperConversion.toEntity(entity);
            handler.notifyRemoved(world, bEntity);
        }

        @Override
        public int size() {
            return this.base.size();
        }

        @Override
        public boolean isEmpty() {
            return this.base.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            return this.base.containsKey(key);
        }

        @Override
        public boolean containsValue(Object value) {
            return this.base.containsValue(value);
        }

        @Override
        public Object get(Object key) {
            return this.base.get(key);
        }

        @Override
        public Object put(UUID key, Object value) {
            Object rval = this.base.put(key, value);
            if (value != rval) {
                if (rval != null) {
                    this.onRemoved(rval);
                }
                if (value != null) {
                    this.onAdded(value);
                }
            }
            return rval;
        }

        @Override
        public Object remove(Object key) {
            Object removed = this.base.remove(key);
            if (removed != null) {
                this.onRemoved(removed);
            }
            return removed;
        }

        @Override
        public void putAll(Map<? extends UUID, ? extends Object> m) {
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                this.put((UUID) entry.getKey(), entry.getValue());
            }
        }

        @Override
        public void clear() {
            if (this.base.isEmpty()) {
                return;
            }
            ArrayList<Object> old_values = new ArrayList<Object>(this.values());
            this.base.clear();
            for (Object removed : old_values) {
                this.onRemoved(removed);
            }
        }

        @Override
        public Set<UUID> keySet() {
            return this.base.keySet();
        }

        @Override
        public Collection<Object> values() {
            return this.base.values();
        }

        @Override
        public Set<java.util.Map.Entry<UUID, Object>> entrySet() {
            return this.base.entrySet();
        }
    }

    private void replaceInChunk(Chunk chunk, EntityHandle oldEntity, EntityHandle newEntity) {
        Object chunkHandle = HandleConversion.toChunkHandle(chunk);
        if (chunkHandle != null) {
            this.chunkEntitySliceHandler.replace(chunkHandle, oldEntity, newEntity);
        }
    }

    @SuppressWarnings("unchecked")
    private static void replaceInEntityTracker(EntityHandle entity, EntityHandle oldEntity, EntityHandle newEntity) {
        final EntityTracker trackerMap = WorldUtil.getTracker(newEntity.getBukkitWorld());
        EntityTrackerEntryHandle entry = trackerMap.getEntry(entity.getIdField());
        if (entry != null) {
            // PlayerChunkMap$EntityTracker entity
            EntityHandle entryEntity = entry.getEntity();
            if (entryEntity != null && entryEntity.getIdField() == oldEntity.getIdField()) {
                entry.setEntity(newEntity);
            }

            // EntityTrackerEntry 'tracker' entity
            EntityTrackerEntryStateHandle stateHandle = entry.getState();
            EntityHandle stateEntity = stateHandle.getEntity();
            if (stateEntity != null && stateEntity.getIdField() == oldEntity.getIdField() && stateEntity.getRaw() != newEntity.getRaw()) {
                stateHandle.setEntity(newEntity);
            }

            // EntityTrackerEntry List of passengers
            List<Object> statePassengers = (List<Object>) EntityTrackerEntryStateHandle.T.opt_passengers.raw.get(stateHandle.getRaw());
            replaceInList(statePassengers, oldEntity, newEntity);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static boolean replaceInList(List list, EntityHandle oldEntity, EntityHandle newEntity) {
        if (list == null) {
            return false;
        }
        ListIterator<Object> iter = list.listIterator();
        while (iter.hasNext()) {
            Object obj = iter.next();
            if (obj instanceof EntityHandle) {
                EntityHandle obj_e = (EntityHandle) obj;
                if (obj_e.getIdField() == oldEntity.getIdField()) {
                    iter.set(newEntity);
                }
            } else if (EntityHandle.T.isAssignableFrom(obj)) {
                int obj_id = EntityHandle.T.idField.getInteger(obj);
                if (obj_id == oldEntity.getIdField()) {
                    iter.set(newEntity.getRaw());
                }
            }
        }
        return false;
    }
}