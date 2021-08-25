package com.wyvencraft.items.enums;

import com.wyvencraft.items.managers.ItemManager;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public enum ArmorType {
    HELMET(EquipmentSlot.HEAD), CHESTPLATE(EquipmentSlot.CHEST), LEGGINGS(EquipmentSlot.LEGS), BOOTS(EquipmentSlot.FEET);

    final EquipmentSlot slot;

    ArmorType(EquipmentSlot slot) {
        this.slot = slot;
    }

    public static ArmorType matchType(final ItemStack itemStack) {
        if (ItemManager.isAirOrNull(itemStack)) return null;
        String type = itemStack.getType().name();
        if (type.endsWith("_HELMET") || type.endsWith("_SKULL") || type.endsWith("_HEAD")) return HELMET;
        else if (type.endsWith("_CHESTPLATE") || type.equals("ELYTRA")) return CHESTPLATE;
        else if (type.endsWith("_LEGGINGS")) return LEGGINGS;
        else if (type.endsWith("_BOOTS")) return BOOTS;
        else return null;
    }
}
