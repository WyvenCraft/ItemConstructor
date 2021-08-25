package com.wyvencraft.items.deprecated;

import com.wyvencraft.items.data.ItemRecipe;
import com.wyvencraft.items.menus.GuiItems;
import com.wyvencraft.smartinventory.Icon;
import com.wyvencraft.smartinventory.InventoryContents;
import com.wyvencraft.smartinventory.InventoryProvider;
import com.wyvencraft.smartinventory.util.SlotPos;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class CraftingMenuProvider implements InventoryProvider {
    private final ItemRecipe recipe;

    public CraftingMenuProvider(ItemRecipe recipe) {
        this.recipe = recipe;
    }

    @Override
    public void init(@NotNull InventoryContents contents) {
        contents.fill(Icon.cancel(GuiItems.BORDER_ITEM.itemStack));
        if (recipe == null) {
            contents.fillRect(SlotPos.of(1, 2), SlotPos.of(3, 4), Icon.EMPTY);
            contents.set(SlotPos.of(2, 6), Icon.EMPTY);
        } else {
            for (Integer slot : recipe.getRecipe().keySet()) {
                final ItemStack ingred = recipe.getRecipe().getOrDefault(slot, new ItemStack(Material.AIR));

                int row = (11 + slot) % 9;
                int col = (11 + slot) / 9;
                contents.set(SlotPos.of(row, col), Icon.cancel(ingred));
            }
        }
    }
}
