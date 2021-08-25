package com.wyvencraft.items.enums;

import org.bukkit.inventory.EquipmentSlot;

public enum ItemType {
    // ARMOR
    HELMET("_HELMET", Category.ARMOR, EquipmentSlot.HEAD),
    CHEST("_CHESTPLATE", Category.ARMOR, EquipmentSlot.CHEST),
    LEGS("_LEGGINGS", Category.ARMOR, EquipmentSlot.LEGS),
    FEET("_BOOTS", Category.ARMOR, EquipmentSlot.FEET),
    // COMBAT (BOW is also crossbows)
    SWORD("_SWORD", Category.COMBAT, EquipmentSlot.HAND),
    BOW("_BOW", Category.COMBAT, EquipmentSlot.HAND),
    BATTLE_AXE("_BATTLEAXE", Category.COMBAT, EquipmentSlot.HAND),
    SHIELD("_SHIELD", Category.COMBAT, EquipmentSlot.HAND),
    // TOOLS
    PICKAXE("_PICKAXE", Category.TOOLS, EquipmentSlot.HAND),
    AXE("_AXE", Category.TOOLS, EquipmentSlot.HAND),
    SHOVEL("_SHOVEL", Category.TOOLS, EquipmentSlot.HAND),
    HOE("_HOE", Category.TOOLS, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND),
    LIGHTER("_LIGHTER", Category.TOOLS, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND),
    FISHING_ROD("_ROD", Category.TOOLS, EquipmentSlot.HAND),
    // MISCELLANEOUS
    ORB("_ORB", Category.MISCELLANEOUS, EquipmentSlot.HAND),
    ITEM("_ITEM", Category.MISCELLANEOUS, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND),
    POTION("_POTION", Category.MISCELLANEOUS, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND),
    BLOCK("_BLOCK", Category.MISCELLANEOUS, EquipmentSlot.HAND, EquipmentSlot.OFF_HAND);

    final String ending;
    final Category category;
    final EquipmentSlot[] slots;

    ItemType(String end, Category category, EquipmentSlot... slots) {
        this.ending = end;
        this.category = category;
        this.slots = slots;
    }

    public static ItemType matchType(String name) {
        for (ItemType type : values()) {
            if (name.endsWith(type.ending)) return type;
        }
        return null;
    }

    public EquipmentSlot[] getSlots() {
        return slots;
    }

    public Category getCategory() {
        return category;
    }
}
