package com.wyvencraft.items.data;

import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class Item {

    private final String name;
    private final ItemStack item;
    private final NamespacedKey key;
    private Recipe recipe;
    private final boolean hasRecipe;

    public Item(String name, ItemStack item, NamespacedKey key, boolean hasRecipe) {
        this.name = name;
        this.item = item;
        this.key = key;
        this.recipe = null;
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

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }
}
