package com.wyvencraft.items.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.ArmorPiece;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.events.FullSetBuffEvent;
import com.wyvencraft.items.events.FullSetDebuffEvent;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

public class ArmorSetListener implements Listener {

    WyvenItems addon;
    WyvenAPI plugin;

    public ArmorSetListener(WyvenItems addon) {
        this.addon = addon;
        plugin = addon.getPlugin();
    }

    @EventHandler
    public void onEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getNewItem() != null && e.getNewItem().getType() != Material.AIR) {
            PersistentDataContainer pdc = ItemStackBuilder.from(e.getNewItem()).meta().getPersistentDataContainer();
            if (!pdc.has(WyvenItems.getItemKey(), PersistentDataType.STRING)) return;

            ArmorPiece armorPiece = addon.getItemManager().getArmorPiece(e.getNewItem());
            if (armorPiece == null) return;

            if (!armorPiece.getBonusAttributes().isEmpty()) {
                // Apply attributes
            }

            ArmorSet set = addon.getItemManager().getArmorSet(armorPiece.getItem().getName());
            if (set != null) {
                Player player = e.getPlayer();

                if (set.hasFullSet(player)) {
                    FullSetBuffEvent event = new FullSetBuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        // Apply set bonus
                        player.sendMessage("Applied set bonus");
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUnEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getOldItem() != null && e.getOldItem().getType() != Material.AIR) {
            PersistentDataContainer pdc = ItemStackBuilder.from(e.getOldItem()).meta().getPersistentDataContainer();
            if (!pdc.has(WyvenItems.getItemKey(), PersistentDataType.STRING)) return;

            ArmorPiece armorPiece = addon.getItemManager().getArmorPiece(e.getOldItem());
            if (armorPiece == null) return;

            if (!armorPiece.getBonusAttributes().isEmpty()) {
                // Remove piece attributes
            }

            ArmorSet set = addon.getItemManager().getArmorSet(armorPiece.getItem().getName());
            if (set != null) {
                Player player = e.getPlayer();

                if (!set.hasFullSet(player)) {
                    FullSetDebuffEvent event = new FullSetDebuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        // Debuff bonus attributes
                        player.sendMessage("Set bonus Debuff");
                    }
                }
            }
        }
    }
}
