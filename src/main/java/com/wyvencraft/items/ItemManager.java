package com.wyvencraft.items;

import com.wyvencraft.common.ItemBuilder;
import com.wyvencraft.common.PDCItem;
import com.wyvencraft.interfaces.IWyvenCore;
import com.wyvencraft.items.items.Dummy;
import com.wyvencraft.items.items.GrapplingHook;
import com.wyvencraft.items.items.StaticItem;
import com.wyvencraft.items.recipes.Recipe;
import com.wyvencraft.player.PlayerStats;
import com.wyvencraft.player.WyvenPlayer;
import com.wyvencraft.utils.Debug;
import com.wyvencraft.utils.Methods;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemManager {
    private WyvenItems addon;
    private final IWyvenCore plugin;

    public ItemManager(WyvenItems addon) {
        this.addon = addon;
        this.plugin = addon.getPlugin();

        headwearableKey = new NamespacedKey(plugin.getPlugin(), "helmet");

        loadItems();
        new GrapplingHook();
        new Dummy();

        for (Item cItem : customItems) {
            if (cItem.isHasRecipe()) {
                cItem.setRecipe(new Recipe(cItem, false));
            }
        }

//        Bukkit.getPluginManager().registerEvents(new CraftingItemListener(), plugin);
    }

    private final NamespacedKey headwearableKey;

    public List<Item> customItems = new ArrayList<>();
    public List<ArmorSet> armorSets = new ArrayList<>();
    public List<ArmorPiece> armorPieces = new ArrayList<>();

    public void loadItems() {
        FileConfiguration itemsFile = plugin.getConfig("items");
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

                NamespacedKey itemKey = new NamespacedKey(plugin, name);

                Item cItem = new Item(name, stack, itemKey, hasRecipe, false);

                customItems.add(cItem);

//                if (isWearable(stack)) {
//                    Map<Attribute, Double> bonusAttributes = new HashMap<>();
//                    for (Attribute attribute : Attribute.values()) {
//                        if (itemsSection.get(name + "." + attribute.name().toLowerCase()) != null) {
//                            bonusAttributes.put(attribute, (double) itemsSection.getInt(name + "." + attribute.name().toLowerCase()));
//                        }
//                    }
//
//                    armorPieces.add(new ArmorPiece(cItem, bonusAttributes));
//                }
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

    public ItemStack loadStaticItem(StaticItem item) {
        final String path = "STATIC_ITEMS." + item.name();

        ConfigurationSection section = plugin.getConfig("items").getConfigurationSection(path + ".item");

        if (section == null) {
            plugin.getLogger().severe("Could not find an item at: " + path);
            return null;
        }

        ItemBuilder builder;
        if (item == StaticItem.GRAPPLINGHOOK) {
            builder = new ItemBuilder().toItemBuilder(section, Material.FISHING_ROD);
//                    .withPDCLong(GrapplingHook.cooldown, 0);

        } else if (item == StaticItem.DUMMY) {
            builder = new ItemBuilder().toItemBuilder(section, Material.ARMOR_STAND);
        } else {
            throw new IllegalStateException("Unexpected value: " + item);
        }

        ItemStack stack = builder
                .withPDCString(plugin.getKey(), item.name())
                .withItemFlag(
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_POTION_EFFECTS).withUnbreakable(true)
                .build();

        NamespacedKey itemKey = new NamespacedKey(plugin.getPlugin(), item.name());

        boolean hasRecipe = false;
        if (section.get(item.name() + ".recipe-enabled") != null) {
            hasRecipe = section.getBoolean(item.name() + ".recipe-enabled");
        }

        Item cItem = new Item(item.name(), stack, itemKey, hasRecipe, true);

        customItems.add(cItem);

        Debug.log("successfully loaded " + item.name());

        return stack;
    }

    private ItemBuilder createBuilder(ConfigurationSection section, String name) {

        ItemBuilder builder = new ItemBuilder().toItemBuilder(section)
                .withPDCString(plugin.getKey(), name)
                .withItemFlag(
                        ItemFlag.HIDE_UNBREAKABLE,
                        ItemFlag.HIDE_ATTRIBUTES,
                        ItemFlag.HIDE_ENCHANTS,
                        ItemFlag.HIDE_DESTROYS,
                        ItemFlag.HIDE_PLACED_ON,
                        ItemFlag.HIDE_POTION_EFFECTS);

        for (int i = 0; i < builder.getLore().size(); i++) {
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
        WyvenPlayer wp = plugin.getStatsManager().getPlayer(p.getUniqueId());

        if (wp.hasUnlockedRecipe(item.getRecipe())) {
            p.sendMessage(Methods.capitalizeWord(item.getName()) + " recipes have already been unlocked");
            return;
        }

        wp.unlockRecipe(item.getRecipe());
        p.sendMessage("you have unlocked a new recipe for " + Methods.capitalizeWord(item.getName()));
    }

    public void lockRecipe(Player p, Item item) {
        WyvenPlayer wp = plugin.getStatsManager().getPlayer(p.getUniqueId());

        if (!wp.hasUnlockedRecipe(item.getRecipe())) {
            p.sendMessage("You havent unlocked " + Methods.capitalizeWord(item.getName()));
            return;
        }

        wp.lockRecipe(item.getRecipe());
        p.sendMessage("you no longer have access to " + Methods.capitalizeWord(item.getName()) + " recipe");

    }

    public void giveSet(Player p, ArmorSet set) {
        for (int i = 0; i < set.getPieces().size(); i++) {
            Item piece = getArmorPiece(set.getPieces().get(i)).getItem();

            giveItem(p, piece, 1);
        }
    }

    public void giveItem(Player p, Item item, int amount) {

        ItemStack[] stacks = new ItemStack[amount];

        for (int i = 0; i < amount; i++) {
            stacks[i] = item.getItem();
        }

        Methods.addItemsToPlayer(p, p.getLocation(), stacks);
    }

    public Item getCustomItem(String name) {
        for (Item item : customItems) {
            if (item.getName().equalsIgnoreCase(name)) {
                return item;
            }
        }
        return null;
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
        PDCItem pdc = new PDCItem(itemStack);
        if (pdc.hasKey(headwearableKey)) return true;
        else if (type.endsWith("_CHESTPLATE") || type.endsWith("ELYTRA")) return true;
        else if (type.endsWith("_LEGGINGS")) return true;
        else return type.endsWith("_BOOTS");
    }
}