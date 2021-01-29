package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveSetSubCMD extends SubCommand {
    private final WyvenItems addon;

    public GiveSetSubCMD(WyvenItems addon, String name, String permission, int minArgs) {
        super(addon.getPlugin(), name, permission, minArgs);
        this.addon = addon;
    }

    // /customitems giveset <set> [player]


    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player target;
        ArmorSet armorSet;

        if (args.length == 2) {
            if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                getPlugin().getLangManager().sendMessage(sender, "ITEMS.INVALID_ARMOR_SET", r -> r.replace("{0}", args[1]));
                return;
            }

            armorSet = addon.getItemManager().getArmorSetFromID(args[1]);

            if (sender instanceof Player)
                target = (Player) sender;
            else {
                getPlugin().getLangManager().sendMessage(sender, "MISSING_TARGET", r -> r.replace("{0}", args[1]));
                return;
            }
        } else if (args.length == 3) {
            if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                getPlugin().getLangManager().sendMessage(sender, "ITEMS.INVALID_ARMOR_SET", r -> r.replace("{0}", args[1]));
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
