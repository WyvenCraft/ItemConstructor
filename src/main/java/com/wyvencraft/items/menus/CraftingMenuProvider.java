package com.wyvencraft.items.menus;

import com.wyvencraft.items.data.ItemRecipe;
import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.InventoryProvider;
import io.github.portlek.smartinventory.util.SlotPos;
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
        contents.fill(Icon.cancel(ItemsMenuProvider.GuiItem.BORDER_ITEM.getStack()));
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
