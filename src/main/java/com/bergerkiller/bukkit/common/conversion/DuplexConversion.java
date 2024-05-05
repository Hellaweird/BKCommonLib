package com.bergerkiller.bukkit.common.conversion;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.bergerkiller.generated.net.minecraft.world.level.saveddata.maps.MapIconHandle;
import org.bukkit.Chunk;
import org.bukkit.Difficulty;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.map.MapCursor;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import static com.bergerkiller.mountiplex.conversion.type.DuplexConverter.pair;
import static com.bergerkiller.bukkit.common.conversion.Conversion.*;

import com.bergerkiller.bukkit.common.bases.IntVector2;
import com.bergerkiller.bukkit.common.bases.IntVector3;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.nbt.CommonTag;
import com.bergerkiller.bukkit.common.nbt.CommonTagCompound;
import com.bergerkiller.bukkit.common.nbt.CommonTagList;
import com.bergerkiller.bukkit.common.utils.CommonUtil;
import com.bergerkiller.bukkit.common.wrappers.BlockData;
import com.bergerkiller.bukkit.common.wrappers.ChatText;
import com.bergerkiller.bukkit.common.wrappers.ChunkSection;
import com.bergerkiller.bukkit.common.wrappers.DataWatcher;
import com.bergerkiller.bukkit.common.wrappers.EntityTracker;
import com.bergerkiller.bukkit.common.wrappers.IntHashMap;
import com.bergerkiller.bukkit.common.wrappers.LongHashSet;
import com.bergerkiller.bukkit.common.wrappers.PlayerAbilities;
import com.bergerkiller.generated.net.minecraft.nbt.NBTBaseHandle;
import com.bergerkiller.mountiplex.conversion.Conversion;
import com.bergerkiller.mountiplex.conversion.Converter;
import com.bergerkiller.mountiplex.conversion.type.DuplexConverter;
import com.bergerkiller.mountiplex.reflection.declarations.TypeDeclaration;

public class DuplexConversion {
    static {
        if (!CommonBootstrap.isCommonServerInitialized()) {
            throw new IllegalStateException("CommonBootstrap must be bootstrapped before duplex conversion can be used");
        }
    }

    @SuppressWarnings({"rawtypes"})
    public static final DuplexConverter NONE = DuplexConverter.createNull(TypeDeclaration.OBJECT);

    public static final DuplexConverter<Object, Entity> entity = findByPath("net.minecraft.world.entity.Entity", Entity.class);
    public static final DuplexConverter<Object, Player> player = findByPath("net.minecraft.server.level.EntityPlayer", Player.class);
    public static final DuplexConverter<Object[], ItemStack[]> itemStackArr = findByPath("net.minecraft.world.item.ItemStack[]", ItemStack[].class);
    public static final DuplexConverter<Object, World> world = findByPath("net.minecraft.server.level.WorldServer", World.class);
    public static final DuplexConverter<Object, Chunk> chunk = findByPath("net.minecraft.world.level.chunk.Chunk", Chunk.class);
    public static final DuplexConverter<Object, ItemStack> itemStack = findByPath("net.minecraft.world.item.ItemStack", ItemStack.class);
    public static final DuplexConverter<Object, Inventory> inventory = pair(toInventory, toInventoryHandle);
    public static final DuplexConverter<Object, Difficulty> difficulty = pair(toDifficulty, toDifficultyHandle);
    public static final DuplexConverter<Object, GameMode> gameMode = pair(toGameMode, toGameModeHandle);
    public static final DuplexConverter<Object, DataWatcher> dataWatcher = pair(toDataWatcher, toDataWatcherHandle);
    public static final DuplexConverter<Object, DataWatcher.Key<?>> dataWatcherKey = findByPath("net.minecraft.network.syncher.DataWatcherObject", DataWatcher.Key.class);
    public static final DuplexConverter<Object, DataWatcher.Item<?>> dataWatcherItem = findByPath("net.minecraft.network.syncher.DataWatcher.Item", DataWatcher.Item.class);
    public static final DuplexConverter<Object, DataWatcher.PackedItem<?>> dataWatcherPackedItem = findByPath("net.minecraft.network.syncher.DataWatcher.PackedItem", DataWatcher.PackedItem.class);
    public static final DuplexConverter<Object, CommonTag> commonTag = pair(toCommonTag, toNBTTagHandle);
    public static final DuplexConverter<Object, CommonTagCompound> commonTagCompound = findByPath("net.minecraft.nbt.NBTTagCompound", CommonTagCompound.class);
    public static final DuplexConverter<Object, CommonTagList> commonTagList = findByPath("net.minecraft.nbt.NBTTagList", CommonTagList.class);
    public static final DuplexConverter<Integer, Object> paintingFacing = pair(toPaintingFacing, toPaintingFacingId);
    public static final DuplexConverter<Object, IntVector3> blockPosition = findByPath("net.minecraft.core.BlockPosition", IntVector3.class);
    public static final DuplexConverter<Object, IntVector2> chunkIntPair = findByPath("net.minecraft.world.level.ChunkCoordIntPair", IntVector2.class);
    public static final DuplexConverter<Object, Vector> vector = findByPath("net.minecraft.world.phys.Vec3D", Vector.class);
    public static final DuplexConverter<Object, PlayerAbilities> playerAbilities = pair(toPlayerAbilities, toPlayerAbilitiesHandle);
    public static final DuplexConverter<Object, EntityTracker> entityTracker = pair(toEntityTracker, toEntityTrackerHandle);
    public static final DuplexConverter<Object, LongHashSet> longHashSet = pair(toLongHashSet, toLongHashSetHandle);
    public static final DuplexConverter<Object, IntHashMap<Object>> intHashMap = pair(toIntHashMap, toIntHashMapHandle);
    public static final DuplexConverter<Object, BlockState> blockState = pair(toBlockState, toTileEntityHandle);
    public static final DuplexConverter<Object, Material> block = pair(toMaterial, toBlockHandle);
    public static final DuplexConverter<Object, Material> item = pair(toMaterial, toItemHandle);
    public static final DuplexConverter<Object, UUID> gameProfileId = pair(toGameProfileId, toGameProfileFromId);
    public static final DuplexConverter<Object, BlockData> blockData = pair(toBlockData, toBlockDataHandle);
    public static final DuplexConverter<Object, ChunkSection> chunkSection = pair(toChunkSection, toChunkSectionHandle);
    public static final DuplexConverter<Object, PotionEffectType> potionEffectType = pair(toPotionEffectType, toMobEffectList);
    public static final DuplexConverter<Object, PotionEffect> potionEffect = pair(toPotionEffect, toMobEffect);
    public static final DuplexConverter<Object, MapCursor> mapCursor = pair(toMapCursor, toMapIconHandle);
    public static final DuplexConverter<Object, ChatText> chatText = pair(toChatText, toChatComponentHandle);

    // Collection element transformation
    public static final DuplexConverter<List<Object>, List<Entity>> entityList = pairElem(List.class, entity);
    public static final DuplexConverter<Collection<Object>, Collection<Entity>> entityCollection = pairElem(Collection.class, entity);
    public static final DuplexConverter<List<Object>, List<Player>> playerList = pairElem(List.class, player);
    public static final DuplexConverter<Set<Object>, Set<Player>> playerSet = pairElem(Set.class, player);
    public static final DuplexConverter<Collection<Object>, Collection<Chunk>> chunkCollection = pairElem(Collection.class, chunk);
    public static final DuplexConverter<List<Object>, List<ItemStack>> itemStackList = pairElem(List.class, itemStack);
    public static final DuplexConverter<List<Object>, List<DataWatcher>> dataWatcherList = pairElem(List.class, dataWatcher);
    public static final DuplexConverter<List<Object>, List<DataWatcher.Item<?>>> dataWatcherItemList = pairElem(List.class, dataWatcherItem);
    public static final DuplexConverter<Collection<Object>, Collection<BlockState>> blockStateCollection = pairElem(Collection.class, blockState);
    public static final DuplexConverter<Object[], ChatText[]> chatTextArray = pairArray(chatText);
    public static final DuplexConverter<Object[], ChunkSection[]> chunkSectionArray = pairArray(chunkSection);
    public static final DuplexConverter<Object[], MapCursor[]> mapCursorArray = pairArray(mapCursor);

    public static final DuplexConverter<Object, CommonTag> nbtBase_commonTag = new DuplexConverter<Object, CommonTag>(CommonUtil.getClass("net.minecraft.nbt.NBTBase", false), CommonTag.class) {
        @Override
        public CommonTag convertInput(Object value) {
            return NBTBaseHandle.createHandleForData(value).toCommonTag();
        }

        @Override
        public Object convertOutput(CommonTag value) {
            return value.getRawHandle();
        }
    };
    public static final DuplexConverter<Object, CommonTag> nbtBase_commonTag_readOnly = new DuplexConverter<Object, CommonTag>(CommonUtil.getClass("net.minecraft.nbt.NBTBase", false), CommonTag.class) {
        @Override
        public CommonTag convertInput(Object value) {
            return CommonTag.makeReadOnly(NBTBaseHandle.createHandleForData(value).toCommonTag());
        }

        @Override
        public Object convertOutput(CommonTag value) {
            return value.getRawHandle();
        }
    };
    public static final DuplexConverter<Object, NBTBaseHandle> nbtBase_nbtBaseHandle = new DuplexConverter<Object, NBTBaseHandle>(CommonUtil.getClass("net.minecraft.nbt.NBTBase", false), NBTBaseHandle.class) {
        @Override
        public NBTBaseHandle convertInput(Object value) {
            return NBTBaseHandle.createHandleForData(value);
        }

        @Override
        public Object convertOutput(NBTBaseHandle value) {
            return value.getRaw();
        }
    };
    public static final DuplexConverter<String, String> string_string = DuplexConverter.createNull(TypeDeclaration.fromClass(String.class));

    @SuppressWarnings("unchecked")
    private static final <T> T pairElem(Class<?> type, DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createGeneric(type, elementConverter.input);
        TypeDeclaration output = TypeDeclaration.createGeneric(type, elementConverter.output);
        return (T) Conversion.findDuplex(input, output);
    }

    @SuppressWarnings("unchecked")
    private static final <T> T pairArray(DuplexConverter<?, ?> elementConverter) {
        TypeDeclaration input = TypeDeclaration.createArray(elementConverter.input.type);
        TypeDeclaration output = TypeDeclaration.createArray(elementConverter.output.type);
        return (T) Conversion.findDuplex(input, output);
    }

    private static final <T> T findByPath(String nmsClassName, Class<?> output) {
        Class<?> type = CommonUtil.getClass(nmsClassName, false);
        if (type == null) {
            throw new IllegalStateException("Class does not exist for duplex converter: " + nmsClassName);
        }
        return find(type, output);
    }

    @SuppressWarnings("unchecked")
    private static final <T> T find(Class<?> input, Class<?> output) {
        Converter<?, ?> conv = Conversion.find(input, output);
        if (conv == null) {
            throw new IllegalStateException("Failed to find converter from " + input + " to " + output);
        }

        Converter<?, ?> conv_rev = Conversion.find(output, input);
        if (conv_rev == null) {
            throw new IllegalStateException("Failed to find reverse converter from " + output + " to " + input);
        }

        return (T) pair(conv, conv_rev);
    }
}
