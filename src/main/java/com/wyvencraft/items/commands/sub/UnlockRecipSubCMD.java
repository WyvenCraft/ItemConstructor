package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.utils.Text;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.utils.Message;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlockRecipSubCMD extends SubCommand {
    private final WyvenItems addon;

    public UnlockRecipSubCMD(WyvenItems addon, String name, String permission, int minArgs) {
        super(addon.getPlugin(), name, permission, minArgs);
        this.addon = addon;
    }

    // /customitem unlock <recipe> [player]

    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        Player target = sender instanceof Player ? (Player) sender : null;
        Item item;

        if (args.length == 0) {
            sender.sendMessage(Text.color("&cUsage: /wi unlock <recipe> [player]"));
            return;
        }

        item = addon.getItemManager().getCustomItem(args[0]);

        if (item == null) {
            getPlugin().getLanguageManager().sendMessage(sender, Message.INVALID_ITEM_MESSAGE.getPath(), r -> r.replace("{0}", args[0]));
            return;
        }

        if (args.length >= 2) {
            target = Bukkit.getPlayer(args[1]);
            if (target == null) {
                getPlugin().getLanguageManager().sendMessage(sender, "INVALID_PLAYER", r -> r.replace("{0}", args[1]));
                return;
            }
        }

        if (target == null) {
            getPlugin().getLanguageManager().sendMessage(sender, "MISSING_TARGET", r -> r.replace("{0}", "(players only)"));
            return;
        }

        if (item.hasRecipe()) {
            getPlugin().getLanguageManager().sendMessage(sender, Message.NO_RECIPE_MESSAGE.getPath(), r -> r.replace("{0}", args[1]));
            return;
        }

        // UNLOCK RECIPE
        if (!target.hasDiscoveredRecipe(item.getKey()))
            addon.getRecipeManager().unlockRecipe(target, item);
        else {
            final String targetName = target.getName();
            getPlugin().getLanguageManager().sendMessage(target, Message.ALREADY_UNLOCKED_MESSAGE.getPath(), r -> r.replace("{0}", args[1]).replace("{1}", targetName));
        }
    }
}
