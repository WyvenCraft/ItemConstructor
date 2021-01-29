package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class LockRecipeSubCMD extends SubCommand {
    private final WyvenItems addon;

    public LockRecipeSubCMD(WyvenItems addon, String name, String permission, int minArgs) {
        super(addon.getPlugin(), name, permission, minArgs);
        this.addon = addon;
    }

    // /customitem unlock <recipe> [player]

    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player target;
        Item item;
        if (args.length == 2) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                getPlugin().getLangManager().sendMessage(sender, "ITEMS.INVALID_ITEM", r -> r.replace("{0}", args[1]));
                return;
            }

            item = addon.getItemManager().getCustomItem(args[1]);

            if (sender instanceof Player)
                target = (Player) sender;
            else {
                getPlugin().getLangManager().sendMessage(sender, "MISSING_TARGET", r -> r.replace("{0}", "NULL"));
                return;
            }

        } else if (args.length == 3) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                getPlugin().getLangManager().sendMessage(sender, "ITEMS.INVALID_ITEM", r -> r.replace("{0}", args[1]));
                return;
            }

            item = addon.getItemManager().getCustomItem(args[1]);

            target = Bukkit.getPlayer(args[2]);
            if (target == null) return;

        } else {
            sender.sendMessage("Usage: /customitem lock <recipe> [player]");
            return;
        }

        if (!item.isHasRecipe()) {
            getPlugin().getLangManager().sendMessage(sender, "ITEMS.NO_RECIPE", r -> r.replace("{0}", args[1]));
            return;
        }

        if (target.hasDiscoveredRecipe(item.getKey()))
            addon.getItemManager().lockRecipe(target, item);
        else
            getPlugin().getLangManager().sendMessage(sender, "ITEMS.NOT_UNLOCKED", r -> r.replace("{0}", target.getName()).replace("{1}", args[1]));
    }
}
