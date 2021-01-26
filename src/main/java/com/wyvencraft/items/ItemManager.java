package com.wyvencraft.items;


import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.items.Dummy;
import com.wyvencraft.items.items.GrapplingHook;
import com.wyvencraft.items.recipes.Recipe;
import com.wyvencraft.items.utils.Debug;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

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

        loadItems();

        new GrapplingHook();
        new Dummy();

        for (Item cItem : customItems) {
            if (cItem.isHasRecipe()) {
                cItem.setRecipe(new Recipe(cItem, false));
            }
        }
    }


    public void loadItems() {
        FileConfiguration itemsFile = plugin.getConfig("items.yml");
        ConfigurationSection itemsSection = itemsFile.getConfigurationSection("ITEMS");

        customItems.clear();
        armorSets.clear();
        armorPieces.clear();

        if (itemsSection != null) {
            for (String name : itemsSection.getKeys(false)) {
                ConfigurationSection itemSection = itemsSection.getConfigurationSection(name + ".item");

                ItemBuilder builder = createBuilder(itemSection, name);

                if (name.endsWith("_HELMET")) builder.withPDCBoolean(headwearableKey, true);

                if (itemSection.getConfigurationSection("enchants") != null) {
                    ConfigurationSection enchSection = itemSection.getConfigurationSection("enchants");
                    for (String ench : enchSection.getKeys(false)) {
                        Enchantment enchantment = Enchant.getEnchantment(ench);
                        if (enchantment != null) {
                            builder.withEnchantment(enchantment, enchSection.getInt(ench));
                        } else {
                            Debug.log("couldn't find an enchantment named " + ench);
                        }
                    }
                }

                if (itemSection.getString("material").startsWith("LEATHER_")) {
                    if (itemSection.get("color") != null) {
                        builder.withColor(itemSection.getString("color"));
                    }
                }

                boolean hasRecipe = false;
                if (itemsSection.get(name + ".recipe-enabled") != null) {
                    hasRecipe = itemsSection.getBoolean(name + ".recipe-enabled");
                }

                ItemStack stack = builder.build();
                if (stack == null) {
                    Debug.log("Skipping " + name + " couldnt load item.");
                    continue;
                }

                NamespacedKey itemKey = new NamespacedKey(plugin.getPlugin(), name);

                Item cItem = new Item(name, stack, itemKey, hasRecipe, false);

                customItems.add(cItem);
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

    private ItemStackBuilder createBuilder(ConfigurationSection section, String name) {

        ItemStackBuilder builder = ItemStackBuilder.from()
                .meta().getPersistentDataContainer().set(WyvenItems.getItemKey(), PersistentDataType.STRING, name);

        for (int i = 0; i < builder.meta().getLore().size(); i++) {
            String line = builder.getLore().get(i);

//            if (line.contains("{bonus:")) {
//                String ability = line.substring(7, line.length() - 1);
//                if (plugin.getAbility(ability) == null) {
//                    plugin.getLogger().severe("could not find ability: " + ability);
//                    continue;
//                }
//                Ability bonusAbility = plugin.getAbility(ability);
//
//                builder.getLore().remove(line);
//                List<String> bonusDesc = new ArrayList<String>() {{
//                    //                        bonusLine = PlaceholderAPI.setPlaceholders((OfflinePlayer) p, bonusLine);
//                    this.addAll(Arrays.asList(bonusAbility.getDescription()));
//                }};
//
//                builder.getLore().addAll(i, bonusDesc);
//            }

            builder.getLore().set(i, line);

        }

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

        //Methods.addItemsToPlayer(p, p.getLocation(), stacks);
    }

    public Item getCustomItem(String name) {
        return customItems.stream()
                .filter(item -> item.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }

    public ArmorPiece getArmorPiece(ItemStack stack) {
        for (ArmorPiece piece : armorPieces) {
            if (piece.getItem().getItem().isSimilar(stack)) return piece;
        }
        return null;
    }

    public ArmorPiece getArmorPiece(String name) {
        for (ArmorPiece piece : armorPieces) {
            if (piece.getItem().getKey().getKey().equals(name)) return piece;
        }
        return null;
    }

    public ArmorSet getArmorSetFromID(String id) {
        for (ArmorSet set : armorSets) if (set.getId().equals(id)) return set;
        return null;
    }

    public ArmorSet getArmorSet(String itemPiece) {
        for (ArmorSet set : armorSets) {
            if (set.getPieces().contains(itemPiece)) return set;
        }
        return null;
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