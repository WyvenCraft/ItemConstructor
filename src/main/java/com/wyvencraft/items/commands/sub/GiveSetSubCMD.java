package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveSetSubCMD extends SubCommand {
    private final WyvenItems addon;
    private final WyvenAPI plugin;

    public GiveSetSubCMD(WyvenAPI plugin, WyvenItems addon, String name, String permission, int minArgs) {
        super(plugin, name, permission, minArgs);
        this.addon = addon;
        this.plugin = plugin;
    }

    // /customitems giveset <set> [player]


    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player target;
        ArmorSet armorSet;

        if (args.length == 2) {
            if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_armorset");
                return;
            }

            armorSet = addon.getItemManager().getArmorSetFromID(args[1]);

            if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage("Please specify player after <set>");
                return;
            }
        } else if (args.length == 3) {
            if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_armorset");
                return;
            }

            armorSet = addon.getItemManager().getArmorSetFromID(args[1]);

            target = Bukkit.getPlayer(args[2]);

            if (target == null) {
                return;
            }

        } else {
            sender.sendMessage("Usage: /customitem giveset <set> [player]");
            return;
        }

        addon.getItemManager().giveSet(target, armorSet);
    }
}
