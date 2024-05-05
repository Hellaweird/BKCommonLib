package com.bergerkiller.generated.net.minecraft.network.protocol;

import com.bergerkiller.mountiplex.reflection.declarations.Template;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.Packet</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.Packet")
public abstract class PacketHandle extends Template.Handle {
    /** @see PacketClass */
    public static final PacketClass T = Template.Class.create(PacketClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public static Iterable<Object> tryUnwrapBundlePacket(Object packet) {
        return T.tryUnwrapBundlePacket.invoker.invoke(null,packet);
    }

    public com.bergerkiller.bukkit.common.protocol.CommonPacket toCommonPacket() {
        return new com.bergerkiller.bukkit.common.protocol.CommonPacket(getRaw(), getPacketType());
    }

    public com.bergerkiller.bukkit.common.protocol.PacketType getPacketType() {
        return com.bergerkiller.bukkit.common.protocol.PacketType.getType(getRaw());
    }

    protected final double getProtocolPosition(Template.Field.Byte field_1_8_8, Template.Field.Integer field_1_10_2) {
        if (field_1_10_2.isAvailable()) {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_10_2(field_1_10_2.getInteger(getRaw()));
        } else {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8((int) field_1_8_8.getByte(getRaw()));
        }
    }

    protected final void setProtocolPosition(Template.Field.Byte field_1_8_8, Template.Field.Integer field_1_10_2, double position) {
        if (field_1_10_2.isAvailable()) {
            field_1_10_2.setInteger(getRaw(), com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_10_2(position));
        } else {
            field_1_8_8.setByte(getRaw(), (byte) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(position));
        }
    }

    protected final double getProtocolPosition(Template.Field.Integer field_1_8_8, Template.Field.Double field_1_10_2) {
        if (field_1_10_2.isAvailable()) {
            return field_1_10_2.getDouble(getRaw());
        } else {
            return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializePosition_1_8_8(field_1_8_8.getInteger(getRaw()));
        }
    }

    protected final void setProtocolPosition(Template.Field.Integer field_1_8_8, Template.Field.Double field_1_10_2, double position) {
        if (field_1_10_2.isAvailable()) {
            field_1_10_2.setDouble(getRaw(), position);
        } else {
            field_1_8_8.setInteger(getRaw(), com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializePosition_1_8_8(position));
        }
    }

    protected final float getProtocolRotation(Template.Field.Byte field) {
        return com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.deserializeRotation((int) field.getByte(getRaw()));
    }

    protected final void setProtocolRotation(Template.Field.Byte field, float rotation) {
        field.setByte(getRaw(), (byte) com.bergerkiller.bukkit.common.internal.logic.ProtocolMath.serializeRotation(rotation));
    }

    protected final com.bergerkiller.bukkit.common.wrappers.HumanHand internalGetHand(Template.Field.Converted<Object> field, org.bukkit.entity.HumanEntity humanEntity) {
        if (field.isAvailable()) {
            return com.bergerkiller.bukkit.common.wrappers.HumanHand.fromNMSEnumHand(humanEntity, field.get(getRaw()));
        } else {
            return com.bergerkiller.bukkit.common.wrappers.HumanHand.RIGHT;
        }
    }

    protected final void internalSetHand(Template.Field.Converted<Object> field, org.bukkit.entity.HumanEntity humanEntity, com.bergerkiller.bukkit.common.wrappers.HumanHand hand) {
        if (field.isAvailable()) {
            field.set(getRaw(), com.bergerkiller.bukkit.common.wrappers.HumanHand.toNMSEnumHand(humanEntity, hand));
        }
    }
    /**
     * Stores class members for <b>net.minecraft.network.protocol.Packet</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketClass extends Template.Class<PacketHandle> {
        public final Template.StaticMethod<Iterable<Object>> tryUnwrapBundlePacket = new Template.StaticMethod<Iterable<Object>>();

    }

}

