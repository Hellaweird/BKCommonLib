package com.bergerkiller.bukkit.common.internal.logic;

import java.io.File;

import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;

import com.bergerkiller.bukkit.common.controller.PlayerDataController;
import com.bergerkiller.bukkit.common.conversion.type.HandleConversion;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.generated.net.minecraft.server.level.WorldServerHandle;
import com.bergerkiller.generated.net.minecraft.server.players.PlayerListHandle;
import com.bergerkiller.mountiplex.reflection.SafeField;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.declarations.SourceDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastField;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;
import com.bergerkiller.reflection.org.bukkit.craftbukkit.CBCraftServer;

/**
 * Handler for Minecraft 1.8 to Minecraft 1.15.2
 */
class PlayerFileDataHandler_1_8_to_1_15_2 extends PlayerFileDataHandler {
    private final FastMethod<File> getPlayerFolderOfWorld = new FastMethod<File>();
    private final FastField<Object> playerListFileDataField;

    public PlayerFileDataHandler_1_8_to_1_15_2() {
        ClassResolver resolver = new ClassResolver();
        resolver.setPackage("net.minecraft.server");
        resolver.setDeclaredClass(WorldServerHandle.T.getType());

        {
            MethodDeclaration getPlayerFolderOfWorldMethod = new MethodDeclaration(resolver, SourceDeclaration.preprocess(
                    "public java.io.File getPlayerDir() {\n" +
                    "    return ((net.minecraft.server.WorldNBTStorage) instance.getDataManager()).getPlayerDir();\n" +
                    "}"));
            getPlayerFolderOfWorld.init(getPlayerFolderOfWorldMethod);  
        }

        Class<?> playerFileDataType = CommonUtil.getClass("net.minecraft.world.level.storage.IPlayerFileData");
        playerListFileDataField = CommonUtil.unsafeCast(SafeField.create(PlayerListHandle.T.getType(), "playerFileData", playerFileDataType).getFastField());
    }

    @Override
    public void enable() {
    }

    @Override
    public void disable() {
    }

    @Override
    public void forceInitialization() {
        this.getPlayerFolderOfWorld.forceInitialization();
        this.playerListFileDataField.forceInitialization();
    }

    @Override
    public PlayerDataController get() {
        PlayerFileDataHook hook = update(HookAction.GET);
        return (hook == null) ? null : hook.controller;
    }

    @Override
    public Hook hook(PlayerDataController controller) {
        PlayerFileDataHook hook = update(HookAction.HOOK);
        if (hook != null) {
            hook.controller = controller;
        }
        return hook;
    }

    @Override
    public Hook mock(PlayerDataController controller) {
        return update(HookAction.MOCK);
    }

    @Override
    public void unhook(Hook hook, PlayerDataController controller) {
        if (hook instanceof PlayerFileDataHook) {
            PlayerFileDataHook p_hook = (PlayerFileDataHook) hook;
            if (p_hook.controller == controller) {
                update(HookAction.UNHOOK);
            }
        }
    }

    @Override
    public File getPlayerDataFolder(World world) {
        return getPlayerFolderOfWorld.invoke(HandleConversion.toWorldHandle(world));
    }

    public CommonTagCompound migratePlayerData(CommonTagCompound playerProfileData) {
        return playerProfileData;
    }

    public PlayerFileDataHook update(HookAction action) {
        Object playerList = CBCraftServer.getPlayerList.invoke(Bukkit.getServer());
        Object playerFileData = playerListFileDataField.get(playerList);

        // Get the player file data hook or hook a new one
        PlayerFileDataHook hook = PlayerFileDataHook.get(playerFileData, PlayerFileDataHook.class);
        if (action == HookAction.GET) {
            return hook;
        } else if ((hook == null) && (action != HookAction.UNHOOK)) {
            hook = new PlayerFileDataHook();
            if (action == HookAction.MOCK) {
                hook.mock(playerFileData);
            } else {
                playerListFileDataField.set(playerList, hook.hook(playerFileData));
            }
        } else if ((hook != null) && (action == HookAction.UNHOOK)) {
            playerListFileDataField.set(playerList, PlayerFileDataHook.unhook(playerFileData));
            hook = new PlayerFileDataHook();
            hook.mock(playerFileData);
        }
        return hook;
    }

    public static enum HookAction {
        HOOK, UNHOOK, MOCK, GET
    }
}
