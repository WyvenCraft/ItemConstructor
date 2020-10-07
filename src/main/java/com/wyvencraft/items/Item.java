package com.wyvencraft.items;

import com.wyvencraft.wyvencore.customitems.recipes.Recipe;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;

public class Item {

    private final String name;
    private final ItemStack item;
    private final NamespacedKey key;
    private Recipe recipe;
    private final boolean hasRecipe;
    private final boolean isStatic;

    public Item(String name, ItemStack item, NamespacedKey key, boolean hasRecipe, boolean isStatic) {
        this.name = name;
        this.item = item;
        this.key = key;
        this.recipe = null;
        this.hasRecipe = hasRecipe;
        this.isStatic = isStatic;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public boolean isHasRecipe() {
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
