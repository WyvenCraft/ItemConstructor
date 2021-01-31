package com.wyvencraft.items.menus;

import io.github.portlek.smartinventory.InventoryProvider;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import org.bukkit.entity.Player;

public final class ItemsMenu {
    final SmartInventory inventory;

    final InventoryProvider provider;

    public ItemsMenu(SmartInventory inventory, InventoryProvider provider) {
        this.inventory = inventory;
        this.provider = provider;
    }

    public void open(Player player) {
        Page.build(inventory, provider).title("Custom Items").row(6).open(player);
    }
}
