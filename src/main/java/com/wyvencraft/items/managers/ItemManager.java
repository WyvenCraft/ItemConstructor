package com.wyvencraft.items.managers;


import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.data.ArmorPiece;
import com.wyvencraft.items.data.ArmorSet;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.utils.Utils;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Stream;

public class ItemManager {
    private final WyvenItems addon;
    private final WyvenAPI plugin;

    private final NamespacedKey HELMETKEY;

    public List<Item> customItems = new ArrayList<>();
    public List<ArmorSet> armorSets = new ArrayList<>();
    public List<ArmorPiece> armorPieces = new ArrayList<>();

    public ItemManager(WyvenItems addon) {
        this.addon = addon;
        this.plugin = addon.getPlugin();

        HELMETKEY = new NamespacedKey(plugin.getPlugin(), "helmet");
    }


    public void loadItems() {
        FileConfiguration itemsFile = addon.getConfig("items.yml");
        ConfigurationSection itemsSection = itemsFile.getConfigurationSection("ITEMS");

        customItems.clear();
        armorSets.clear();
        armorPieces.clear();

        if (itemsSection != null) {
            for (String name : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(name + ".item");

                ItemStackBuilder builder = getBuilder(itemSection);

                // Setup attributes
                ConfigurationSection statsSection = itemsSection.getConfigurationSection(name + ".stats");
                if (statsSection != null) {
                    List<String> statsLoreSection = new ArrayList<>();

                    final String equipSlotStr = name.toUpperCase();

                    final EquipmentSlot slot;

                    if (equipSlotStr.endsWith("_HELMET")) slot = EquipmentSlot.HEAD;
                    else if (equipSlotStr.endsWith("_CHESTPLATE")) slot = EquipmentSlot.CHEST;
                    else if (equipSlotStr.endsWith("_LEGGINGS")) slot = EquipmentSlot.LEGS;
                    else if (equipSlotStr.endsWith("_BOOTS")) slot = EquipmentSlot.FEET;
                    else slot = null;

                    for (String attrName : statsSection.getKeys(false)) {
                        Attribute attribute;
                        try {
                            attribute = Attribute.valueOf("GENERIC_" + attrName.toUpperCase());
                        } catch (IllegalArgumentException e) {
                            plugin.getLogger().warning(attrName + " is not a valid attribute");
                            continue;
                        }

                        final int amount = statsSection.getInt(attrName);
                        final double multiplier = (double) amount / 100;
                        AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attrName, multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1, slot);

                        StringBuilder stat = new StringBuilder("&7" + WordUtils.capitalizeFully(attrName.replaceAll("_", " ")) + ": ");
                        if (amount >= 0) stat.append("&a+");
                        else stat.append("&c");
                        stat.append(amount).append('%');
                        statsLoreSection.add(stat.toString());

                        builder.addAttributeModifier(attribute, modifier);
                    }

                    if (!statsLoreSection.isEmpty()) {
                        List<String> oldLore = Optional.ofNullable(builder.meta().getLore()).orElse(new ArrayList<>());
                        statsLoreSection.addAll(oldLore);

                        builder.lore(statsLoreSection, true);
                    }
                }

                // JUST FOR NOW
                final boolean hasRecipe = false;

                final NamespacedKey itemKey = new NamespacedKey(plugin.getPlugin(), name.toLowerCase());

                customItems.add(new Item(name, builder.itemStack(), itemKey, hasRecipe));
            }
        }

        ConfigurationSection setsSection = itemsFile.getConfigurationSection("ARMOR_SETS");
        if (setsSection != null) {
            for (String id : setsSection.getKeys(false)) {
                List<String> fullsetBonus = setsSection.getStringList(id + ".fullset_bonus");
                List<String> pieces = new ArrayList<>();
                for (String pieceName : setsSection.getStringList(id + ".pieces")) {
                    Item piece = getCustomItem(pieceName);

                    if (piece == null) {
                        plugin.getLogger().severe("could not load " + pieceName + " for " + id);
                        continue;
                    }

                    pieces.add(pieceName);
                }

                armorSets.add(new ArmorSet(id, pieces, fullsetBonus));
            }
        }
    }

    public ItemStackBuilder getBuilder(ConfigurationSection section) {
        if (section == null) return null;

        final Material material = Material.getMaterial(Objects.requireNonNull(section.getString("material", "null")));
        if (material == null) {
            plugin.getLogger().warning("Missing material: " + section.getName());
            return null;
        }

        ItemStackBuilder builder = ItemStackBuilder.from(material);

        builder.flag(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);

        if (material.name().startsWith("LEATHER_")) {
            if (section.contains("color")) {
                final LeatherArmorMeta meta = (LeatherArmorMeta) builder.meta();
                meta.setColor(Utils.hexToRgb(section.getString("color")));
            }
        }

        final String name = section.getString("name", material.name());

        if (name != null) builder.name(name, true);

        if (section.contains("enchants")) {
            builder.enchantments(String.valueOf(section.getStringList("enchants")));
        }

        final List<String> lore = section.getStringList("lore");

        builder.lore(lore, true);

        return builder;
    }

    public void unlockRecipe(Player p, Item item) {
        if (p.hasDiscoveredRecipe(item.getKey())) {
            p.sendMessage(item.getName() + " recipes have already been unlocked");
            return;
        }

        p.discoverRecipe(item.getKey());

        p.sendMessage("you have unlocked a new recipe for " + item.getName());
    }

    public void lockRecipe(Player p, Item item) {
        if (!p.hasDiscoveredRecipe(item.getKey())) {
            p.sendMessage("You havent unlocked " + item.getName());
            return;
        }

        p.undiscoverRecipe(item.getKey());
        p.sendMessage("you no longer have access to " + item.getName() + " recipe");
    }

    public void giveSet(Player p, ArmorSet set) {
        for (String armorPiece : set.getPieces()) {
            Item piece = getArmorPiece(armorPiece).getItem();

            giveItem(p, piece, 1);
        }
    }

    public void giveItem(Player p, Item item, int amount) {
        ItemStack[] stacks = new ItemStack[amount];

        for (int i = 0; i < amount; i++) {
            stacks[i] = item.getItem();
        }

        final Map<Integer, ItemStack> fallenItems = p.getInventory().addItem(
                Stream.of(stacks).filter(Objects::nonNull).toArray(ItemStack[]::new));

        /* If map is not empty, some items were not added... Let's drop them. */
        if (!fallenItems.isEmpty()) {
            fallenItems.values().forEach(stack -> p.getWorld().dropItemNaturally(p.getLocation(), stack));
        }
    }

    public Item getCustomItem(String name) {
        return customItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ArmorPiece getArmorPiece(ItemStack stack) {
        return armorPieces.stream()
                .filter(a -> a.getItem().getItem().isSimilar(stack))
                .findFirst()
                .orElse(null);
    }

    public ArmorPiece getArmorPiece(String name) {
        return armorPieces.stream()
                .filter(a -> a.getItem().getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ArmorSet getArmorSetFromID(String id) {
        return armorSets.stream()
                .filter(set -> set.getId().equalsIgnoreCase(id))
                .findFirst()
                .orElse(null);
    }

    public ArmorSet getArmorSet(String itemPiece) {
        return armorSets.stream()
                .filter(set -> set.getPieces().contains(itemPiece))
                .findFirst()
                .orElse(null);
    }

    public boolean isWearable(final ItemStack itemStack) {
        String type = itemStack.getType().name();

        PersistentDataContainer pdc = ItemStackBuilder.from(itemStack).meta().getPersistentDataContainer();

        if (pdc.has(HELMETKEY, PersistentDataType.STRING)) return true;
        else if (type.endsWith("_CHESTPLATE") || type.endsWith("ELYTRA")) return true;
        else if (type.endsWith("_LEGGINGS")) return true;
        else return type.endsWith("_BOOTS");
    }
}