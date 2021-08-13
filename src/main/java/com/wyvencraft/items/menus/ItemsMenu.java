package com.wyvencraft.items.menus;


import io.github.portlek.smartinventory.InventoryProvider;
import io.github.portlek.smartinventory.Page;
import io.github.portlek.smartinventory.SmartInventory;
import org.bukkit.entity.Player;

public record ItemsMenu(SmartInventory inventory,
                        InventoryProvider provider) {

    public void open(Player player) {
        Page.build(inventory, provider).title("Custom Items").row(6).open(player);
    }
}
