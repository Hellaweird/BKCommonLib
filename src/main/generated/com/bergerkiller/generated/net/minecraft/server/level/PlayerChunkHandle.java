package com.bergerkiller.generated.net.minecraft.server.level;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.bases.IntVector2;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import java.util.Collection;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.level.PlayerChunk</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.server.level.PlayerChunk")
public abstract class PlayerChunkHandle extends Template.Handle {
    /** @see PlayerChunkClass */
    public static final PlayerChunkClass T = Template.Class.create(PlayerChunkClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PlayerChunkHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract PlayerChunkMapHandle getPlayerChunkMap();
    public abstract boolean resendChunk();
    public abstract boolean resendAllLighting();
    public abstract Collection<Player> getPlayers();
    public abstract IntVector2 getLocation();
    public abstract Chunk getChunkIfLoaded();
    /**
     * Stores class members for <b>net.minecraft.server.level.PlayerChunk</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PlayerChunkClass extends Template.Class<PlayerChunkHandle> {
        public final Template.Method.Converted<PlayerChunkMapHandle> getPlayerChunkMap = new Template.Method.Converted<PlayerChunkMapHandle>();
        public final Template.Method<Boolean> resendChunk = new Template.Method<Boolean>();
        public final Template.Method<Boolean> resendAllLighting = new Template.Method<Boolean>();
        public final Template.Method.Converted<Collection<Player>> getPlayers = new Template.Method.Converted<Collection<Player>>();
        public final Template.Method.Converted<IntVector2> getLocation = new Template.Method.Converted<IntVector2>();
        public final Template.Method.Converted<Chunk> getChunkIfLoaded = new Template.Method.Converted<Chunk>();

    }

}

