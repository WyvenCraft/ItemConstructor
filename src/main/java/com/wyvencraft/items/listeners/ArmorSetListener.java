package com.wyvencraft.items.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.wyvencraft.common.PDCItem;
import com.wyvencraft.interfaces.IWyvenCore;
import com.wyvencraft.items.ArmorPiece;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.events.FullSetBuffEvent;
import com.wyvencraft.items.events.FullSetDebuffEvent;
import com.wyvencraft.player.PlayerStats;
import com.wyvencraft.utils.CmdAction;
import org.apache.logging.log4j.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorSetListener implements Listener {

    WyvenItems addon;
    IWyvenCore plugin;

    public ArmorSetListener(WyvenItems addon){
        this.addon = addon;
        plugin = addon.getPlugin();
    }

    @EventHandler
    public void onEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getNewItem() != null && e.getNewItem().getType() != Material.AIR) {
            PDCItem pdc = new PDCItem(e.getNewItem());

            if (!pdc.hasKey(plugin.getPlugin().WYVENKEY)) return;

            ArmorPiece armorPiece = addon.getItemManager().getArmorPiece(e.getNewItem());
            if (armorPiece == null) return;

            PlayerStats ps = plugin.getStatsManager().getPlayerStats(e.getPlayer().getUniqueId());

            if (!armorPiece.getBonusAttributes().isEmpty()) {
                armorPiece.getBonusAttributes().forEach((attr, amount) -> AttributesHandler.instance.add(ps, attr, amount, true));
            }

            ArmorSet set = plugin.getItemManager().getArmorSet(pdc.getPDCString(plugin.WYVENKEY).toUpperCase());
            if (set != null) {
                Player p = e.getPlayer();

                if (set.isWearing(p)) {
                    FullSetBuffEvent event = new FullSetBuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        CmdAction.fullSetBonus(ps, set, false);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onUnEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getOldItem() != null && e.getOldItem().getType() != Material.AIR) {
            PDCItem pdc = new PDCItem(e.getOldItem());
            if (!pdc.hasKey(plugin.WYVENKEY)) return;

            ArmorPiece customItem = plugin.getItemManager().getArmorPiece(e.getOldItem());
            if (customItem == null) return;

            PlayerStats ps = plugin.getStatsManager().getPlayerStats(e.getPlayer().getUniqueId());

            if (!customItem.getBonusAttributes().isEmpty()) {
                customItem.getBonusAttributes().forEach((attr, amount) -> AttributesHandler.instance.take(ps, attr, amount, true));
            }

            ArmorSet set = plugin.getItemManager().getArmorSet(pdc.getPDCString(plugin.WYVENKEY).toUpperCase());
            if (set != null) {
                Player p = e.getPlayer();

                if (!set.isWearing(p)) {
                    FullSetDebuffEvent event = new FullSetDebuffEvent(e.getPlayer(), set);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        CmdAction.fullSetBonus(ps, set, true);
                    }
                }
            }
        }
    }
}
