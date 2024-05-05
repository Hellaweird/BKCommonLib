package com.bergerkiller.generated.net.minecraft.network.protocol.game;

import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.resources.ResourceKey;
import com.bergerkiller.bukkit.common.resources.SoundEffect;
import com.bergerkiller.generated.net.minecraft.network.protocol.PacketHandle;

/**
 * Instance wrapper handle for type <b>net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
@Template.InstanceType("net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect")
public abstract class PacketPlayOutNamedSoundEffectHandle extends PacketHandle {
    /** @see PacketPlayOutNamedSoundEffectClass */
    public static final PacketPlayOutNamedSoundEffectClass T = Template.Class.create(PacketPlayOutNamedSoundEffectClass.class, com.bergerkiller.bukkit.common.Common.TEMPLATE_RESOLVER);
    /* ============================================================================== */

    public static PacketPlayOutNamedSoundEffectHandle createHandle(Object handleInstance) {
        return T.createHandle(handleInstance);
    }

    /* ============================================================================== */

    public float getPitch() {
        if (T.pitch_1_10_2.isAvailable()) {
            return T.pitch_1_10_2.getFloat(getRaw());
        } else {
            return (float) T.pitch_1_8_8.getInteger(getRaw()) / 63.0f;
        }
    }

    public void setPitch(float pitch) {
        if (T.pitch_1_10_2.isAvailable()) {
            T.pitch_1_10_2.setFloat(getRaw(), pitch);
        } else {
            T.pitch_1_8_8.setInteger(getRaw(), (int) (pitch * 63.0f));
        }
    }

    public String getCategory() {
        if (T.category_1_10_2.isAvailable()) {
            return T.category_1_10_2.get(getRaw());
        } else {
            return "master";
        }
    }

    public void setCategory(String categoryName) {
        if (T.category_1_10_2.isAvailable()) {
            T.category_1_10_2.set(getRaw(), categoryName);
        } else {
        }
    }
    public abstract ResourceKey<SoundEffect> getSound();
    public abstract void setSound(ResourceKey<SoundEffect> value);
    public abstract int getX();
    public abstract void setX(int value);
    public abstract int getY();
    public abstract void setY(int value);
    public abstract int getZ();
    public abstract void setZ(int value);
    public abstract float getVolume();
    public abstract void setVolume(float value);
    /**
     * Stores class members for <b>net.minecraft.network.protocol.game.PacketPlayOutNamedSoundEffect</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class PacketPlayOutNamedSoundEffectClass extends Template.Class<PacketPlayOutNamedSoundEffectHandle> {
        public final Template.Field.Converted<ResourceKey<SoundEffect>> sound = new Template.Field.Converted<ResourceKey<SoundEffect>>();
        @Template.Optional
        public final Template.Field.Converted<String> category_1_10_2 = new Template.Field.Converted<String>();
        public final Template.Field.Integer x = new Template.Field.Integer();
        public final Template.Field.Integer y = new Template.Field.Integer();
        public final Template.Field.Integer z = new Template.Field.Integer();
        public final Template.Field.Float volume = new Template.Field.Float();
        @Template.Optional
        public final Template.Field.Integer pitch_1_8_8 = new Template.Field.Integer();
        @Template.Optional
        public final Template.Field.Float pitch_1_10_2 = new Template.Field.Float();

    }

}

