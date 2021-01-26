package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlockRecipSubCMD extends SubCommand {
    private final WyvenItems addon;
    private final WyvenAPI plugin;

    public UnlockRecipSubCMD(WyvenAPI plugin, WyvenItems addon, String name, String permission, int minArgs) {
        super(plugin, name, permission, minArgs);
        this.addon = addon;
        this.plugin = plugin;
    }
    
    // /customitem unlock <recipe> [player]

    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player target;
        Item item;
        if (args.length == 2) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_item");
                return;
            }

            item = addon.getItemManager().getCustomItem(args[1]);

            if (sender instanceof Player)
                target = (Player) sender;
            else {
                sender.sendMessage("Please specify player after <recipe>");
                return;
            }

        } else if (args.length == 3) {
            if (addon.getItemManager().getCustomItem(args[1]) == null) {
                plugin.getLangManager().sendMessage(sender, "items.invalid_item");
                return;
            }

            item = addon.getItemManager().getCustomItem(args[1]);

            target = Bukkit.getPlayer(args[2]);
            if (target == null) return;
        } else {
            sender.sendMessage("Usage: /customitem unlock <recipe> [player]");
            return;
        }

        if (!item.isHasRecipe()) {
            sender.sendMessage("This item dont have a recipe attached");
            return;
        }

        // UNLOCK RECIPE
        if (!target.hasDiscoveredRecipe(item.getKey()))
            addon.getItemManager().unlockRecipe(target, item);
        else
            sender.sendMessage(target.getName() + " has already unlocked this recipe");

    }
}
