package com.wyvencraft.items.recipes;


import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

public class Recipe {
    private static final FileConfiguration config = WyvenItems.instance.getConfig("items.yml");
    public static final Map<ItemStack, Recipe> customRecipes = new HashMap<>();

    public int id;
    public boolean vanilla;
    public HashMap<Integer, ItemStack> recipe = new HashMap<>();
    public String customItem;
    public ItemStack result;

    public Recipe(Item _customItem, boolean _vanilla) {
        String name = _customItem.getName();
        vanilla = _vanilla;
        customItem = name;
        final String path = _customItem.isStatic() ? "STATIC_ITEMS" : "ITEMS";

        ConfigurationSection slots = config.getConfigurationSection(path + "." + name + ".recipe");
        for (String pos : slots.getKeys(false)) {
            int slot = Methods.getInteger(pos);
            if (slot == -1) {
                plugin.getLogger().severe(pos + " has to be a number from 1 to 9!");
                return;
            }

            if (slot > 9 || slot < 1) {
                plugin.getLogger().severe("Could not load recipe for " + name + ". (slots can only be from 1 to 9)");
                return;
            }

            String[] ingred = slots.getString(pos).split(";", 2);

            int amount = ingred.length > 1 ? Methods.getInteger(ingred[1]) : 1;

            ItemStack item;

            try {
                item = new ItemStack(Material.valueOf(ingred[0].toUpperCase()));
            } catch (IllegalArgumentException e) {
                if (ItemManager.instance.getCustomItem(ingred[0]) != null) {
                    item = ItemManager.instance.getCustomItem(ingred[0]).getItem();
                } else {
                    plugin.getLogger().severe("recipe ingred: " + ingred[0] + ", for item: " + name + ", doesnt exist");
                    continue;
                }
            }

            item.setAmount(amount);
            recipe.put(slot, item);
        }


        result = _customItem.getItem();

        this.id = customRecipes.size();
        customRecipes.put(result, this);
    }

    public boolean canCraft(Inventory inv) {

        ItemStack[] items = Stream.of(inv.getContents()).filter(Objects::nonNull).toArray(ItemStack[]::new);

        Map<ItemStack, Integer> remaining = new HashMap<>();

        int requiredStack = 0;
        for (ItemStack item : recipe.values()) {
            int itemSum = Stream.of(items)
                    .filter(i -> i.isSimilar(item))
                    .mapToInt(ItemStack::getAmount).sum();

            if (itemSum > item.getAmount()) {
                if (remaining.containsKey(item)) {
                    if (remaining.get(item) == 0) return false;

                    itemSum = remaining.get(item);
                }

                if (itemSum >= item.getAmount()) {
                    remaining.put(item, itemSum - item.getAmount());

                    requiredStack++;
                    if (requiredStack == recipe.size()) return true;
                }
            } else return false;
        }

        return false;
    }

    public boolean matches(Map<Integer, ItemStack> grid) {
        for (Map.Entry<Integer, ItemStack> gridItem : grid.entrySet()) {
            if (!recipe.containsKey(gridItem.getKey())) return false;
            ItemStack ingred = recipe.get(gridItem.getKey());
            if (!gridItem.getValue().isSimilar(ingred)) return false;
            if (gridItem.getValue().getAmount() < ingred.getAmount()) return false;
        }
        Debug.log(customItem);
        return true;
    }
}
