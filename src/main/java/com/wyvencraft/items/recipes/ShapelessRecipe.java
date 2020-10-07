package com.wyvencraft.items.recipes;

import com.wyvencraft.wyvencore.customitems.Item;
import com.wyvencraft.wyvencore.customitems.recipes.Recipe;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ShapelessRecipe extends Recipe {
    public ShapelessRecipe(Item _customItem, boolean _vanilla) {
        super(_customItem, _vanilla);
    }

    @Override
    public boolean matches(Map<Integer, ItemStack> grid) {

        return true;
    }
}
