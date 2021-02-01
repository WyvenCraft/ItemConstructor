package com.wyvencraft.items.data;

import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemRecipe {
    public enum ShapeType{
        SHAPELESS, SHAPED;
    }

    private final Map<Integer, ItemStack> recipe;
    private final ShapeType shape;
    private final ItemStack result;

    public ItemRecipe(Map<Integer, ItemStack> recipe, ShapeType shape, ItemStack result) {
        this.recipe = recipe;
        this.shape = shape;
        this.result = result;
    }

    public Map<Integer, ItemStack> getRecipe() {
        return recipe;
    }

    public ShapeType getShape() {
        return shape;
    }

    public ItemStack getResult() {
        return result;
    }
}
