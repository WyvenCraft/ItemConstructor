package com.wyvencraft.items.managers;


import com.wyvencraft.api.language.LanguageManager;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.ArmorPiece;
import com.wyvencraft.items.data.ArmorSet;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.data.ItemRecipe;
import com.wyvencraft.items.enums.Category;
import com.wyvencraft.items.enums.ItemType;
import com.wyvencraft.items.orbs.Orb;
import com.wyvencraft.items.orbs.OrbModifier;
import com.wyvencraft.items.utils.Utils;
import com.wyvencraft.items.utils.Validator;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
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
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ItemManager {
    private final WyvenItems addon;
    private final LanguageManager lang;
    private final RecipeManager recipeManager;

    public List<Item> customItems = new ArrayList<>();
    public List<ArmorSet> armorSets = new ArrayList<>();
    public List<ArmorPiece> armorPieces = new ArrayList<>();

    public ItemManager(WyvenItems addon, LanguageManager lang, RecipeManager recipeManager) {
        this.addon = addon;
        this.lang = lang;
        this.recipeManager = recipeManager;
    }

    public void loadItems() {
        customItems.clear();
        armorSets.clear();
        armorPieces.clear();

        FileConfiguration itemsFile = addon.getConfigurationManager().get("items.yml");
        ConfigurationSection itemsSection = itemsFile.getConfigurationSection("items");
        if (itemsSection != null) {
            for (String name : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(name + ".item");

                addon.getPlugin().printDebug(name);

                ItemStackBuilder builder = getBuilder(itemSection, name);

//                EquipmentSlot slot = getEquipmentSlot(name);

                ItemType type = ItemType.matchType(name);
                if (type == null) {
                    addon.getLogger().warning("'items." + name + "' in items.yml are missing a type");
                    continue;
                }

                builder.getItemMeta().getPersistentDataContainer().set(WyvenItems.ITEM_TYPE, PersistentDataType.STRING, type.name());

                // Setup stat attributes
                ConfigurationSection statsSection = itemsSection.getConfigurationSection(name + ".stats");
                if (statsSection != null) {
                    List<String> statsLoreSection = new ArrayList<>();

                    for (String attrName : statsSection.getKeys(false)) {
                        Attribute attribute = Validator.validateAttribute(attrName);
                        if (attribute == null) continue;

                        final int amount = statsSection.getInt(attrName);
                        final double multiplier = (double) amount / 100;
                        for (EquipmentSlot slot : type.getSlots()) {
                            AttributeModifier modifier = new AttributeModifier(UUID.randomUUID(), attrName, multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1, slot);
                            StringBuilder stat = new StringBuilder("&7" + WordUtils.capitalizeFully(attrName.replaceAll("_", " ")) + ": ");
                            if (amount >= 0) stat.append("&a+");
                            else stat.append("&c");
                            stat.append(amount).append('%');
                            statsLoreSection.add(stat.toString());

                            builder.addAttributeModifier(attribute, modifier);
                        }
                    }

                    if (!statsLoreSection.isEmpty()) {
                        final var serializer = PlainTextComponentSerializer.plainText();
                        final List<Component> statsLore = Objects.requireNonNull(statsLoreSection)
                                .stream()
                                .map(serializer::deserialize)
                                .collect(Collectors.toList());

                        statsLore.addAll(Objects.requireNonNullElse(builder.getItemMeta().lore(), new ArrayList<>()));

                        builder.getItemMeta().lore(statsLore);
                    }
                }

                // Setup recipe
                boolean recipeEnabled = itemsSection.getBoolean(name + ".recipe.enabled", false);
                ItemRecipe recipe = null;
                if (recipeEnabled) {
                    final ConfigurationSection recipeSection = itemsSection.getConfigurationSection(name + ".recipe.shape");
                    if (recipeSection != null) {
                        recipe = recipeManager.createRecipe(recipeSection, builder.getItemStack());
                    } else {
                        addon.getLogger().warning(name + " has recipes enabled, but missing 'recipe.shape'-section");
                        continue;
                    }
                }

                // This NamespacedKey is used for adding crafting recipes
                final NamespacedKey itemKey = new NamespacedKey(addon.getPlugin().getPlugin(), name.toLowerCase());
                Item item = new Item(name, builder.getItemStack(), itemKey, recipe, type);

                switch (type) {
                    case ORB:
                        ConfigurationSection orbSection = Objects.requireNonNull(itemsFile.getConfigurationSection("items." + name + ".orb"),
                                "'items." + name + "' in items.yml has type 'ORB', but no orb-section!");

                        ItemStack skull = ItemStackBuilder.from(Material.PLAYER_HEAD)
                                .asSkull()
                                .setOwner(orbSection.getString("skull", "STEVE"))
                                .getItemStack();

                        List<OrbModifier> orbModifiers = new ArrayList<>();
                        if (orbSection.contains("modifiers")) {
                            for (String id : orbSection.getConfigurationSection("modifiers").getKeys(false)) {
                                AttributeModifier attributeModifier = null;
                                if (orbSection.contains("modifiers." + id + ".attribute")) {
                                    String[] attriStr = orbSection.getString("modifiers." + id + ".attribute").split(";", 2);
                                    Attribute attribute = Validator.validateAttribute(attriStr[0]);
                                    if (attribute == null) continue;
                                    final double multiplier = (double) Utils.getInteger(attriStr[1]) / 100;

                                    attributeModifier = new AttributeModifier(UUID.randomUUID(), attriStr[0], multiplier, AttributeModifier.Operation.MULTIPLY_SCALAR_1);
                                }

//                                PotionEffect potionEffect = null;
//                                if (orbSection.contains("modifiers.potion")) {
//                                    String[] attriStr = orbSection.getString("modifiers.potion").split(";", 2);
//                                }

                                boolean onlyOnInit = orbSection.getBoolean("modifiers." + id + ".onlyInit", false);

                                String targetStr = orbSection.getString("modifiers." + id + ".target", "FRIENDLY");
                                OrbModifier.OrbTarget target = Validator.validateOrbTarget(targetStr);
                                if (target == null) {
                                    addon.getLogger().log(Level.INFO, "Defaulting to FRIENDLY");
                                    target = OrbModifier.OrbTarget.FRIENDLY;
                                }

                                orbModifiers.add(new OrbModifier(null, attributeModifier, target, onlyOnInit));
                            }
                        }

                        final int radius = orbSection.getInt("radius", 3);
                        final double aliveTime = orbSection.getDouble("aliveTime", 8);
                        final double orbCooldown = orbSection.getDouble("cooldown", 30);

                        customItems.add(new Orb(item, skull, orbModifiers, radius, aliveTime, orbCooldown));
                        break;
                    default:
                        customItems.add(item);
                }

            }
        }

        // LOAD ARMOR SETS
    }

    public List<Item> getCategoryItems(Category category) {
        return customItems.stream()
                .filter(i -> i.getType().getCategory() == category)
                .collect(Collectors.toList());
    }

    public ItemStackBuilder getBuilder(ConfigurationSection section, String key) {
        if (section == null) return null;

        String materialStr = section.getString("material", "STONE");

        ItemStackBuilder builder;
        if (materialStr.startsWith("skin-")) {
            String owner = materialStr.split("-")[1];

            ItemStack skull = ItemStackBuilder.from(Material.PLAYER_HEAD)
                    .asSkull()
                    .setOwner(owner)
                    .getItemStack();
            builder = ItemStackBuilder.from(Material.PLAYER_HEAD).setItemStack(skull);
        }
//        else if (materialStr.startsWith("hdb-")) {
//            if (addon.isHookEnabled("HeadDatabase")) {
//                String owner = materialStr.split("-")[1];
//                if (addon.getHead(owner) != null) {
//                    addon.getLogger().log(Level.INFO, "Using HeadDatabase 2");
//                    builder = ItemStackBuilder.from(addon.getHead(owner));
//                }
//            }
//        }
        else {
            final Material material = Material.getMaterial(materialStr.toUpperCase());
            if (material == null) {
                addon.getLogger().warning("Missing material: " + section.getName());
                return null;
            }

            builder = ItemStackBuilder.from(material);

            if (material.name().startsWith("LEATHER_")) {
                if (section.contains("color")) {
                    final LeatherArmorMeta meta = (LeatherArmorMeta) builder.getItemMeta();
                    meta.setColor(Utils.hexToRgb(section.getString("color")));
                }
            }
        }

        builder.addFlag(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DYE);

        final String name = section.getString("name");

        if (name != null) builder.setName(name, true);

        if (section.contains("enchants")) {
            builder.addEnchantments(String.valueOf(section.getStringList("enchants")));
        }

        final List<String> lore = section.getStringList("lore");

        final var serializer = PlainTextComponentSerializer.plainText();
        final List<Component> finalLore = Objects.requireNonNull(lore)
                .stream()
                .map(serializer::deserialize)
                .collect(Collectors.toList());

        builder.getItemMeta().lore(finalLore);
        builder.getItemMeta().getPersistentDataContainer().set(WyvenItems.WYVEN_ITEM, PersistentDataType.STRING, key);

        return builder;
    }

    public boolean isItemOfType(ItemType type, ItemStack hand) {

        if (!isCustomItem(hand)) return false;

        try {
            ItemType itemType = ItemType.valueOf(hand.getItemMeta().getPersistentDataContainer().get(WyvenItems.ITEM_TYPE, PersistentDataType.STRING));
            return type == itemType;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public ItemStack getHand(Player player, boolean checkOffHand) {
        final ItemStack mainHand = player.getInventory().getItemInMainHand();

        // TODO It doesnt check the offhand if there is something in mainhand
        ItemStack holding = checkOffHand && mainHand.getType() == Material.AIR
                ? player.getInventory().getItemInOffHand()
                : mainHand;

        if (holding.getType() == Material.AIR || !holding.hasItemMeta()) return null;

        return holding;
    }

    public boolean isCustomItem(ItemStack stack) {
        return stack.getItemMeta().getPersistentDataContainer().has(WyvenItems.WYVEN_ITEM, PersistentDataType.STRING);
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

    public Item getItem(ItemStack stack) {
        final String customItemName = stack.getItemMeta().getPersistentDataContainer().get(WyvenItems.WYVEN_ITEM, PersistentDataType.STRING);
        return getItem(customItemName);
    }

    public Item getItem(String name) {
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

    public static boolean isAirOrNull(ItemStack stack) {
        return stack == null || stack.getType() == Material.AIR;
    }
}