package com.wyvencraft.items.utils;


import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.orbs.OrbModifier;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.inventory.ItemStack;

public final class Validator {

    public static Attribute validateAttribute(String name) {
        try {
            return Attribute.valueOf("GENERIC_" + name.toUpperCase());
        } catch (IllegalArgumentException e) {
            WyvenItems.instance.getLogger().warning(name + " is not a valid attribute");
            return null;
        }
    }

    public static OrbModifier.OrbTarget validateOrbTarget(String target) {
        try {
            return OrbModifier.OrbTarget.valueOf(target);
        } catch (IllegalArgumentException e) {
            WyvenItems.instance.getLogger().warning(target + " is an invalid OrbTarget use 'FRIENDLY', 'NEUTRAL', 'ENEMY'");
            return null;
        }
    }

    public static ItemStack validateItem(String materialName, int amount) {
        try {
            final Material material = Material.valueOf(materialName);
            return new ItemStack(material, amount);
        } catch (IllegalArgumentException e) {
            // TODO check if its a custom item
            WyvenItems.instance.getLogger().severe(materialName + " is invalid material");
            return null;
        }
    }
}
