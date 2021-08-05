package com.wyvencraft.items.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.ArmorPiece;
import com.wyvencraft.items.data.ArmorSet;
import com.wyvencraft.items.events.FullSetBuffEvent;
import com.wyvencraft.items.events.FullSetDebuffEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorSetListener implements Listener {

    private final WyvenItems addon;
//    private WyvenAPI plugin;

    public ArmorSetListener(WyvenItems addon) {
        this.addon = addon;
//        this.plugin = addon.getPlugin();
    }

    @EventHandler
    public void onEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getNewItem() != null && e.getNewItem().getType() != Material.AIR) {
            if (addon.getItemManager().isCustomItem(e.getNewItem())) return;

            ArmorPiece armorPiece = addon.getItemManager().getArmorPiece(e.getNewItem());
            if (armorPiece == null) return;

            if (!armorPiece.getBonusAttributes().isEmpty()) {
                // Apply attributes
                e.getPlayer().sendMessage("[DEBUG] Applied piece attributes");
            }

            ArmorSet set = addon.getItemManager().getArmorSet(armorPiece.getItem().getName());
            if (set != null) {
                Player player = e.getPlayer();

                if (set.hasFullSet(player)) {
                    FullSetBuffEvent event = new FullSetBuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        // Apply set bonus
                        player.sendMessage("[DEBUG] Applied set bonus");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUnEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getOldItem() != null && e.getOldItem().getType() != Material.AIR) {
            if (addon.getItemManager().isCustomItem(e.getOldItem())) return;

            ArmorPiece armorPiece = addon.getItemManager().getArmorPiece(e.getOldItem());
            if (armorPiece == null) return;

            if (!armorPiece.getBonusAttributes().isEmpty()) {
                // Remove piece attributes
                e.getPlayer().sendMessage("[DEBUG] Removed piece attributes");
            }

            ArmorSet set = addon.getItemManager().getArmorSet(armorPiece.getItem().getName());
            if (set != null) {
                Player player = e.getPlayer();

                if (!set.hasFullSet(player)) {
                    FullSetDebuffEvent event = new FullSetDebuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        // Debuff bonus attributes
                        player.sendMessage("[DEBUG] Set bonus Debuff");
                    }
                }
            }
        }
    }
}
