package com.wyvencraft.items.listeners;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.attributes.AttributesHandler;
import com.wyvencraft.wyvencore.common.PDCItem;
import com.wyvencraft.wyvencore.customitems.ArmorPiece;
import com.wyvencraft.wyvencore.customitems.ArmorSet;
import com.wyvencraft.wyvencore.events.FullSetBuffEvent;
import com.wyvencraft.wyvencore.events.FullSetDebuffEvent;
import com.wyvencraft.wyvencore.player.PlayerStats;
import com.wyvencraft.wyvencore.utils.CmdAction;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ArmorSetListener implements Listener {

    Core plugin = Core.instance;

    @EventHandler
    public void onEquipArmor(PlayerArmorChangeEvent e) {
        if (e.getNewItem() != null && e.getNewItem().getType() != Material.AIR) {
            PDCItem pdc = new PDCItem(e.getNewItem());

            if (!pdc.hasKey(plugin.WYVENKEY)) return;

            ArmorPiece armorPiece = plugin.getItemManager().getArmorPiece(e.getNewItem());
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
