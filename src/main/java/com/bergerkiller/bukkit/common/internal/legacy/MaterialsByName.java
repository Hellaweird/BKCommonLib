package com.bergerkiller.bukkit.common.internal.legacy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import com.bergerkiller.bukkit.common.internal.CommonLegacyMaterials;
import org.bukkit.Material;

import com.bergerkiller.bukkit.common.Logging;
import com.bergerkiller.bukkit.common.conversion.type.WrapperConversion;
import com.bergerkiller.bukkit.common.internal.CommonBootstrap;
import com.bergerkiller.bukkit.common.internal.CommonCapabilities;
import com.bergerkiller.bukkit.common.utils.LogicUtil;
import com.bergerkiller.generated.net.minecraft.world.level.block.BlockHandle;
import com.bergerkiller.generated.net.minecraft.world.level.block.state.IBlockDataHandle;
import com.bergerkiller.generated.org.bukkit.craftbukkit.util.CraftMagicNumbersHandle;
import com.bergerkiller.mountiplex.reflection.declarations.ClassResolver;
import com.bergerkiller.mountiplex.reflection.declarations.MethodDeclaration;
import com.bergerkiller.mountiplex.reflection.util.FastMethod;

/**
 * Stores a mapping of all available materials by their material's name.
 * Legacy materials are prefixed with LEGACY_, also on older versions of Minecraft.
 */
public class MaterialsByName {
    private static final Material[] allMaterialValues;
    private static final Material[] allBlockMaterialValues;
    private static final Map<String, Material> allMaterialValuesByName = new HashMap<String, Material>();
    private static final FastMethod<Boolean> isLegacyMethod = new FastMethod<Boolean>();

    static {
        // This method gets whether a material is legacy, or not
        // On 1.12.2 and before, it always returns true
        // We do it this way so we don't have to initialize all templates to get this to work
        {
            String template = "public boolean isLegacy() {\n";
            if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
                template += "return instance.isLegacy();\n}";
            } else {
                template += "return true;\n}";
            }
            ClassResolver resolver = new ClassResolver();
            resolver.setDeclaredClass(Material.class);
            isLegacyMethod.init(new MethodDeclaration(resolver, template));
        }

        // Retrieve all Material values through reflection
        {
            Material[] values = null;
            try {
                values = (Material[]) Material.class.getMethod("values").invoke(null);
            } catch (Throwable t) {
                Logging.LOGGER_REGISTRY.log(Level.WARNING, "Material values() method not found, trying fallback", t);
                values = Material.class.getEnumConstants();
            }

            // On MC 1.8 there is a LOCKED_CHEST Material that does not actually exist
            // It throws tests off the rails because of the Type Id clash it causes
            // By removing this rogue element from the array we can avoid these problems.
            if (values != null && CommonBootstrap.evaluateMCVersion("==", "1.8")) {
                for (int index = 0; index < values.length; index++) {
                    if (getMaterialName(values[index]).equals("LEGACY_LOCKED_CHEST")) {
                        values = LogicUtil.removeArrayElement(values, index);
                        break;
                    }
                }
            }

            allMaterialValues = values;
        }

        // Fill the allMaterialValuesByName map with LEGACY_ names and new names
        // On versions before 1.13, allow for legacy names in the map normally
        try {
            for (Material material :  MaterialsByName.getAllMaterials()) {
                allMaterialValuesByName.put(getMaterialName(material), material);
            }
        } catch (Throwable t) {
            Logging.LOGGER_REGISTRY.log(Level.SEVERE, "Error while initializing allMaterialValuesByName", t);
        }

        // Fill the allBlockMaterialValues array with all the materials that are blocks
        // Legacy block materials are excluded when on 1.13 or later
        {
            ArrayList<Material> blocks = new ArrayList<Material>();
            for (Object block : BlockHandle.getRegistry()) {
                Material mat = WrapperConversion.toMaterialFromBlockHandle(block);
                if (mat != null) {
                    blocks.add(mat);
                }
            }
            allBlockMaterialValues = blocks.toArray(new Material[0]);
        }
    }

    /**
     * Gets an array of all Material enum values, unaffected by the Spigot Material remapping.<br>
     * <b>Not suitable for use by Plugins</b>
     * 
     * @return all Material enum values
     */
    public static Material[] getAllMaterials() {
        return allMaterialValues;
    }

    /**
     * Gets an array of all Material enum values that are blocks. This array does not contain duplicates
     * due to legacy/non-legacy. On 1.13 and later, no legacy block materials are included.
     * 
     * @return all block material enum values
     */
    public static Material[] getAllBlocks() {
        return allBlockMaterialValues;
    }

    /**
     * Gets the name() of a Material.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param type
     * @return type name
     */
    public static String getMaterialName(Material type) {
        if (CommonCapabilities.MATERIAL_ENUM_CHANGES) {
            return ((Enum<?>) type).name();
        } else {
            return "LEGACY_" + ((Enum<?>) type).name();
        }
    }

    /**
     * Gets an array of materials from material enum names.
     * Any names missing will cause an exception.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.<br>
     * <b>Not suitable for use by Plugins</b>
     * 
     * @param names
     * @return materials
     */
    public static Material[] getAllByName(String... names) {
        Material[] result = new Material[names.length];
        for (int i = 0; i < names.length; i++) {
            Material m = getMaterial(names[i]);
            if (m == null) {
                throw new RuntimeException("Material not found: " + names[i]);
            }
            result[i] = m;
        }
        return result;
    }

    /**
     * Gets a material by name.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param name
     * @return material, null if not found
     */
    public static Material getMaterial(String name) {
        return allMaterialValuesByName.get(name);
    }

    /**
     * Gets a legacy material by name. Prepends LEGACY_ on MC 1.13 and onwards for lookup.
     * The Material remapping performed by Spigot on MC 1.13 is ignored.
     * 
     * @param name
     * @return
     */
    public static Material getLegacyMaterial(String name) {
        return allMaterialValuesByName.get("LEGACY_" + name);
    }

    /**
     * Gets whether a given Material enum value is legacy, or not.
     * Always returns true on MC 1.12.2 and before.
     * 
     * @param material
     * @return True if this is a legacy Material
     */
    public static boolean isLegacy(Material material) {
        return isLegacyMethod.invoke(material);
    }

    /**
     * Gets whether a Material is a type of Block. Ideally use BlockData instead
     * if you can.
     *
     * @param type Type
     * @return True if the material is of a type of block, and not of an item.
     */
    public static boolean isBlock(Material type) {
        if (isLegacy(type)) {
            int id = type.getId();
            return id >= 0 && id < 256;
        } else {
            return type.isBlock();
        }
    }

    /**
     * Helper method to retrieve IBlockData by Material enum name.
     * Only suitable for non-legacy names, and meant to be used before the BlockData API initializes.
     * 
     * @param name
     * @return IBlockData
     */
    public static IBlockDataHandle getBlockDataFromMaterialName(String name) {
        return CraftMagicNumbersHandle.getBlockDataFromMaterial(getMaterial(name));
    }

}
