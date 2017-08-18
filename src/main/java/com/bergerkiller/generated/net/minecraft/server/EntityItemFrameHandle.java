package com.bergerkiller.generated.net.minecraft.server;

import com.bergerkiller.mountiplex.reflection.util.StaticInitHelper;
import com.bergerkiller.mountiplex.reflection.declarations.Template;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher.Key;
import org.bukkit.inventory.ItemStack;

/**
 * Instance wrapper handle for type <b>net.minecraft.server.EntityItemFrame</b>.
 * To access members without creating a handle type, use the static {@link #T} member.
 * New handles can be created from raw instances using {@link #createHandle(Object)}.
 */
public class EntityItemFrameHandle extends EntityHandle {
    /** @See {@link EntityItemFrameClass} */
    public static final EntityItemFrameClass T = new EntityItemFrameClass();
    static final StaticInitHelper _init_helper = new StaticInitHelper(EntityItemFrameHandle.class, "net.minecraft.server.EntityItemFrame");

    /* ============================================================================== */

    public static EntityItemFrameHandle createHandle(Object handleInstance) {
        if (handleInstance == null) return null;
        EntityItemFrameHandle handle = new EntityItemFrameHandle();
        handle.instance = handleInstance;
        return handle;
    }

    /* ============================================================================== */

    public ItemStack getItem() {
        return T.getItem.invoke(instance);
    }

    public void setItem(ItemStack newItemStack) {
        T.setItem.invoke(instance, newItemStack);
    }


    public static final Key<org.bukkit.inventory.ItemStack> DATA_ITEM = Key.Type.ITEMSTACK.createKey(T.DATA_ITEM, 8);


    public static EntityItemFrameHandle fromBukkit(org.bukkit.entity.ItemFrame itemFrame) {
        return createHandle(com.bergerkiller.bukkit.common.conversion.type.HandleConversion.toEntityHandle(itemFrame));
    }
    /**
     * Stores class members for <b>net.minecraft.server.EntityItemFrame</b>.
     * Methods, fields, and constructors can be used without using Handle Objects.
     */
    public static final class EntityItemFrameClass extends Template.Class<EntityItemFrameHandle> {
        @Template.Optional
        public final Template.StaticField.Converted<Key<Object>> DATA_ITEM = new Template.StaticField.Converted<Key<Object>>();

        public final Template.Method.Converted<ItemStack> getItem = new Template.Method.Converted<ItemStack>();
        public final Template.Method.Converted<Void> setItem = new Template.Method.Converted<Void>();

    }

}

