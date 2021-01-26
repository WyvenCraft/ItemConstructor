package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GiveSubCMD extends SubCommand {
    private final WyvenItems addon;
    private final WyvenAPI plugin;

    public GiveSubCMD(WyvenAPI plugin, WyvenItems addon, String name, String permission, int minArgs) {
        super(plugin, name, permission, minArgs);
        this.addon = addon;
        this.plugin = plugin;
    }

    // /customitems give <item> [player] [amount]


    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player giveTarget;
        Item giveItem;
        int amount = 1;

        if (args.length == 2) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_item");
                return;
            }

            giveItem = addon.getItemManager().getCustomItem(args[1]);

            if (sender instanceof Player)
                giveTarget = (Player) sender;
            else {
                sender.sendMessage("Please specify player after <item>");
                return;
            }
        } else if (args.length == 3) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_item");
                return;
            }

            giveItem = addon.getItemManager().getCustomItem(args[1]);

            giveTarget = Bukkit.getPlayer(args[2]);
            if (giveTarget == null) return;

        } else if (args.length == 4) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_item");
                return;
            }

            giveItem = addon.getItemManager().getCustomItem(args[1]);

            giveTarget = Bukkit.getPlayer(args[2]);
            if (giveTarget == null) return;

            try {
                amount = Integer.parseInt(args[3]);
            } catch (NumberFormatException e) {
                plugin.getLangManager().sendMessage(sender, "invalid_number");
            }

            amount = Math.max(amount, 0);
        } else {
            sender.sendMessage("Usage: /customitem give <item> [player] [amount]");
            return;
        }

        addon.getItemManager().giveItem(giveTarget, giveItem, amount);
    }
}
