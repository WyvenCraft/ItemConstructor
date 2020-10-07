package com.wyvencraft.items;

import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.common.ItemBuilder;
import com.wyvencraft.wyvencore.common.Lang;
import com.wyvencraft.wyvencore.menus.Menu;
import com.wyvencraft.wyvencore.menus.MenuSettings;
import com.wyvencraft.wyvencore.utils.Methods;
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import io.github.portlek.smartinventory.util.SlotPos;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class CraftingMenu extends Menu {

    public static CraftingMenu instance;


    public CraftingMenu(Core _plugin, SmartInventory _inventory, InventoryType _type) {
        super(_plugin, _inventory, _type);
        instance = this;
    }

    public final SlotPos[] craftingSlots = new SlotPos[]{
            SlotPos.of(10 / 9, 10 % 9),
            SlotPos.of(11 / 9, 11 % 9),
            SlotPos.of(12 / 9, 12 % 9),
            SlotPos.of(19 / 9, 19 % 9),
            SlotPos.of(20 / 9, 20 % 9),
            SlotPos.of(21 / 9, 21 % 9),
            SlotPos.of(28 / 9, 28 % 9),
            SlotPos.of(29 / 9, 29 % 9),
            SlotPos.of(30 / 9, 30 % 9)};
    public final SlotPos[] recipesSlots = new SlotPos[]{
            SlotPos.of(16 / 9, 16 % 9),
            SlotPos.of(17 / 9, 17 % 9),
            SlotPos.of(25 / 9, 25 % 9),
            SlotPos.of(26 / 9, 26 % 9),
            SlotPos.of(34 / 9, 34 % 9),
            SlotPos.of(35 / 9, 35 % 9)};
    public final SlotPos resultSlot = SlotPos.of(23 / 9, 23 % 9);

    @Override
    public void load() {
        MenuSettings settings = getMenuSettings("Menu");

        ConfigurationSection section = file.getConfigurationSection(type.name() + ".Menu");
        ItemStack invalidRecipe = new ItemBuilder().toItemBuilder(section.getConfigurationSection("invalidRecipe")).build();
        ItemStack lockedRecipe = new ItemBuilder().toItemBuilder(section.getConfigurationSection("lockedRecipe")).build();
        ItemStack recipesSlot = new ItemBuilder().toItemBuilder(section.getConfigurationSection("recipesSlot")).build();

        Page MENU = Page.build(Inventory, new CraftingTableProvider(
                settings.getGuiItems(),
                invalidRecipe,
                lockedRecipe,
                recipesSlot))
                .title(Lang.color(settings.getTitle()))
                .row(settings.getRows())
                .parent(settings.getParent())
                .whenEmptyClick(pageClickEvent -> pageClickEvent.contents().notifyUpdate())
                .whenClose(closeEvent -> returnGrid(closeEvent.contents()));
        Pages.put("Menu", MENU);
    }

    private void returnGrid(InventoryContents contents) {
        Inventory workbench = contents.getTopInventory();
        ItemStack[] items = new ItemStack[9];

        for (int i = 0; i < 9; i++) {
            SlotPos pos = craftingSlots[i];
            int slot = pos.getRow() * 9 + pos.getColumn();

            items[i] = workbench.getItem(slot);
            workbench.setItem(slot, new ItemStack(Material.AIR));
        }

        Methods.addItemsToPlayer(contents.player(), contents.player().getLocation(), items);
    }
}
