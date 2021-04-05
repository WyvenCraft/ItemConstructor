package com.wyvencraft.items.managers;


import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.ArmorPiece;
import com.wyvencraft.items.data.ArmorSet;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.data.ItemRecipe;
import com.wyvencraft.items.enums.ItemType;
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

    private final NamespacedKey HELMET_KEY;

    public List<Item> customItems = new ArrayList<>();
    public List<ArmorSet> armorSets = new ArrayList<>();
    public List<ArmorPiece> armorPieces = new ArrayList<>();

    public ItemManager(WyvenItems addon) {
        this.addon = addon;
        this.plugin = addon.getPlugin();

        HELMET_KEY = new NamespacedKey(plugin.getPlugin(), "helmet");
    }

    public void loadItems() {
        FileConfiguration itemsFile = addon.getConfig("items.yml");
        ConfigurationSection itemsSection = itemsFile.getConfigurationSection("items");

        customItems.clear();
        armorSets.clear();
        armorPieces.clear();

        if (itemsSection != null) {
            for (String name : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(name + ".item");

                ItemStackBuilder builder = getBuilder(itemSection);

                EquipmentSlot slot = getEquipmentSlot(name);

                ItemType type = getItemType(itemsFile.getString("items." + name + ".type", null), builder.itemStack().getType());
                if (type == null) {
                    plugin.getLogger().warning("'items." + name + ".type' in items.yml doesnt exist");
                    continue;
                }

                // Setup attributes
                ConfigurationSection statsSection = itemsSection.getConfigurationSection(name + ".stats");
                if (statsSection != null) {
                    List<String> statsLoreSection = new ArrayList<>();

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

                boolean recipeEnabled = itemsSection.getBoolean(name + ".recipe.enabled", false);
                ItemRecipe recipe = null;
                if (recipeEnabled) {
                    final ConfigurationSection recipeSection = itemsSection.getConfigurationSection(name + ".recipe.shape");
                    if (recipeSection != null) {
                        recipe = createRecipe(recipeSection, builder.itemStack());
                    } else {
                        plugin.getLogger().warning(name + " has recipes enabled, but missing 'recipe.shape'-section");
                        continue;
                    }
                }

                final NamespacedKey itemKey = new NamespacedKey(plugin.getPlugin(), name.toLowerCase());

                customItems.add(new Item(name, builder.itemStack(), itemKey, recipe, type));
            }
        }

        // LOAD ARMOR SETS
    }

    private ItemType getItemType(String value, Material material) {
        try {
            return ItemType.valueOf(value);
        } catch (Exception e) {
            if (material.name().endsWith("_HELMET")) return ItemType.HELMET;
            else if (material.name().endsWith("_CHESTPLATE")) return ItemType.CHESTPLATE;
            else if (material.name().endsWith("_LEGGINGS")) return ItemType.LEGGING;
            else if (material.name().endsWith("_BOOTS")) return ItemType.BOOTS;
            else if (material == Material.BOW || material == Material.CROSSBOW) return ItemType.ARCHERY;
            else return null;
        }
    }

    private EquipmentSlot getEquipmentSlot(String material) {
        final String equipSlotStr = material.toUpperCase();

        final EquipmentSlot slot;

        if (equipSlotStr.endsWith("_HELMET")) slot = EquipmentSlot.HEAD;
        else if (equipSlotStr.endsWith("_CHESTPLATE")) slot = EquipmentSlot.CHEST;
        else if (equipSlotStr.endsWith("_LEGGINGS")) slot = EquipmentSlot.LEGS;
        else if (equipSlotStr.endsWith("_BOOTS")) slot = EquipmentSlot.FEET;
        else slot = null;

        return slot;
    }

    public ItemRecipe createRecipe(ConfigurationSection recipeSection, ItemStack result) {
        Map<Integer, ItemStack> recipe = new HashMap<>();

        for (String raw : recipeSection.getKeys(false)) {
            final int slot = Utils.getInteger(raw);
            if (slot == -1) {
                plugin.getLogger().severe(raw + " has to be a number (0-8).");
                return null;
            }

            ItemStack ingredient;

            final String[] ingredStr = recipeSection.getString(raw).split(";", 2);
            try {
                final Material material = Material.valueOf(ingredStr[0]);
                int amount = ingredStr.length > 1 ? Utils.getInteger(ingredStr[1]) : 1;

                ingredient = new ItemStack(material, amount);

            } catch (IllegalArgumentException e) {
                // TODO check if its a custom item
                plugin.getLogger().severe(ingredStr[0] + " is invalid material");
                continue;
            }

            recipe.put(slot, ingredient);
        }

        return new ItemRecipe(recipe, ItemRecipe.ShapeType.SHAPED, result);
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
        if (!p.discoverRecipe(item.getKey())) {
            p.sendMessage(item.getName() + " recipe have already been unlocked");
            return;
        }

        p.sendMessage("you have unlocked a new recipe for " + item.getName());
    }

    public void lockRecipe(Player p, Item item) {
        if (!p.undiscoverRecipe(item.getKey())) {
            p.sendMessage("You havent unlocked " + item.getName());
            return;
        }

        p.sendMessage("you no longer have access to " + item.getName() + " recipe");
    }

    public void giveSet(Player p, ArmorSet set) {
        for (String pieceID : set.getPieces()) {
            Item piece = getArmorPiece(pieceID).getItem();

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

        if (pdc.has(HELMET_KEY, PersistentDataType.STRING)) return true;
        else {
            return (type.endsWith("_HELMET") ||
                    type.endsWith("_CHESTPLATE") ||
                    type.endsWith("_LEGGINGS") ||
                    type.endsWith("_BOOTS") ||
                    type.equals("ELYTRA"));

        }
    }
}