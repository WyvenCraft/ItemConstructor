package com.wyvencraft.items.menus;


import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.enums.Category;
import com.wyvencraft.items.menus.categories.*;
import com.wyvencraft.smartinventory.Page;
import com.wyvencraft.smartinventory.SmartInventory;
import com.wyvencraft.smartinventory.event.abs.SmartEvent;
import org.bukkit.entity.Player;

import java.util.EnumMap;

public class ItemsMenu {

    private final WyvenItems addon;
    private final SmartInventory inventory;

    private final EnumMap<Category, Page> itemMenus = new EnumMap<>(Category.class);

    public ItemsMenu(WyvenItems addon, SmartInventory inventory) {
        this.addon = addon;
        this.inventory = inventory;
    }

    public void loadItemMenus() {
        Page mainItemsPage = Page.build(inventory, new MainMenuProvider(addon.getItemManager().customItems, this))
                .title("All Items")
                .whenBottomClick(SmartEvent::cancel) // To prevent the player to putting items in free slots
                .row(6);

        Page combatPage = Page.build(inventory, new CombatMenu(addon.getItemManager().getCategoryItems(Category.COMBAT)))
                .title("Items -> Combat")
                .whenBottomClick(SmartEvent::cancel)
                .row(6)
                .parent(mainItemsPage);

        Page armorPage = Page.build(inventory, new ArmorMenu(addon.getItemManager().getCategoryItems(Category.ARMOR)))
                .title("Items -> Armor")
                .whenBottomClick(SmartEvent::cancel)
                .row(6)
                .parent(mainItemsPage);

        Page toolsPage = Page.build(inventory, new ToolsMenu(addon.getItemManager().getCategoryItems(Category.TOOLS)))
                .title("Items -> Tools")
                .whenBottomClick(SmartEvent::cancel)
                .row(6)
                .parent(mainItemsPage);

        Page miscsPage = Page.build(inventory, new MiscellanousMenu(addon.getItemManager().getCategoryItems(Category.MISCELLANEOUS)))
                .title("Items -> Miscellanous")
                .whenBottomClick(SmartEvent::cancel)
                .row(6)
                .parent(mainItemsPage);

        itemMenus.put(Category.MAIN_MENU, mainItemsPage);
        itemMenus.put(Category.COMBAT, combatPage);
        itemMenus.put(Category.ARMOR, armorPage);
        itemMenus.put(Category.TOOLS, toolsPage);
        itemMenus.put(Category.MISCELLANEOUS, miscsPage);
    }

    public void open(Player player, Category category) {
        if (itemMenus.containsKey(category))
            itemMenus.get(category).open(player);
    }
}
