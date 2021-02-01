package com.wyvencraft.items.data;

import com.wyvencraft.items.enums.ItemType;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class Item {

    private final String name;
    private final ItemStack item;
    private final NamespacedKey key;
    private final ItemRecipe recipe;
    private final boolean hasRecipe;
    private final ItemType type;

    public Item(String name, ItemStack item, NamespacedKey key, boolean hasRecipe, ItemRecipe recipe, ItemType type) {
        this.name = name;
        this.item = item;
        this.key = key;
        this.type = type;
        this.recipe = recipe;
        this.hasRecipe = hasRecipe;
    }

    public boolean hasRecipe() {
        return hasRecipe;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public ItemStack getItem() {
        return item;
    }

    public ItemRecipe getRecipe() {
        return recipe;
    }

    public ItemType getType() {
        return type;
    }
}
