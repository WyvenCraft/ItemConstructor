package com.wyvencraft.items.listeners;

import com.wyvencraft.wyvencore.menus.Menu;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.CraftingInventory;

public class CraftingTableOpenListener implements Listener {

    @EventHandler
    public void onOpenWorkbench(InventoryOpenEvent e) {
        if (e.getInventory() instanceof CraftingInventory) {
            e.setCancelled(true);
            Menu.getMenu("Menu").open((Player) e.getPlayer());
        }
    }
}
