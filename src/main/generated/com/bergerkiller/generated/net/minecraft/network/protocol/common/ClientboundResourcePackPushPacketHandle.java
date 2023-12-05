package com.bergerkiller.generated.net.minecraft.network.protocol.common;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;
import java.util.UUID;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket")
public abstract class ClientboundResourcePackPushPacketHandle extends PacketHandle {
    /** @see ClientboundResourcePackPushPacketClass */
    public static final ClientboundResourcePackPushPacketClass T = Template.Class.create(ClientboundResourcePackPushPacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static ClientboundResourcePackPushPacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public abstract void setRequired(boolean required);
    public abstract boolean isRequired();
    public abstract void setPrompt(ChatText prompt);
    public abstract ChatText getPrompt();
    public abstract UUID getId();
    public abstract String getUrl();
    public abstract void setUrl(String value);
    public abstract String getHash();
    public abstract void setHash(String value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.common.ClientboundResourcePackPushPacket</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class ClientboundResourcePackPushPacketClass extends Template.Class<ClientboundResourcePackPushPacketHandle> {
        public final Template.Field<String> url = new Template.Field<String>();
        public final Template.Field<String> hash = new Template.Field<String>();

        public final Template.Method<Void> setRequired = new Template.Method<Void>();
        public final Template.Method<Boolean> isRequired = new Template.Method<Boolean>();
        public final Template.Method.Converted<Void> setPrompt = new Template.Method.Converted<Void>();
        public final Template.Method.Converted<ChatText> getPrompt = new Template.Method.Converted<ChatText>();
        public final Template.Method<UUID> getId = new Template.Method<UUID>();

    }

}

