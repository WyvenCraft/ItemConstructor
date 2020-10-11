package com.wyvencraft.items.commands;

import com.wyvencraft.commands.Permission;
import com.wyvencraft.common.Lang;
import com.wyvencraft.configuration.Message;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.utils.Methods;
import org.apache.logging.log4j.core.Core;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class CustomitemCMD implements CommandExecutor {
    Core plugin = Core.instance;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (sender.hasPermission(Permission.CUSTOMITEMS_HELP.getPerm())) {
                for (String str : plugin.getConfig("lang").getStringList("CUSTOMITEMS.HELP")) {
                    sender.sendMessage(Lang.color(str));
                }
            } else {
                sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                if (sender.hasPermission(Permission.CUSTOMITEMS_HELP.getPerm())) {
                    for (String str : plugin.getConfig("lang").getStringList("CUSTOMITEMS.HELP")) {
                        sender.sendMessage(Lang.color(str));
                    }
                } else {
                    sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
                break;
            // /customitem unlock <recipe> [player]
            case "unlock":
                if (sender.hasPermission(Permission.CUSTOMITEMS_UNLOCK.getPerm())) {
                    Player unlockTarget;
                    Item unlockItem;
                    if (args.length == 2) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        unlockItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            unlockTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <recipe>");
                            return true;
                        }

                    } else if (args.length == 3) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        unlockItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (getTarget(args[2]) == null) {
                            sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
                            return true;
                        }

                        unlockTarget = getTarget(args[2]);
                    } else {
                        sender.sendMessage("Usage: /customitem unlock <recipe> [player]");
                        return true;
                    }

                    if (!unlockItem.isHasRecipe()) {
                        sender.sendMessage("This item dont have a recipe attached");
                        return true;
                    }

                    // UNLOCK RECIPE
                    if (!unlockTarget.hasDiscoveredRecipe(unlockItem.getKey()))
                        plugin.getItemManager().unlockRecipe(unlockTarget, unlockItem);
                    else
                        sender.sendMessage(unlockTarget.getName() + " has already unlocked this recipe");

                } else {
                    sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
                break;
            // /customitem unlock <recipe> [player]
            case "lock":
                if (sender.hasPermission(Permission.CUSTOMITEMS_LOCK.getPerm())) {
                    Player lockTarget;
                    Item lockItem;
                    if (args.length == 2) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        lockItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            lockTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <recipe>");
                            return true;
                        }

                    } else if (args.length == 3) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        lockItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (getTarget(args[2]) == null) {
                            sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
                            return true;
                        }

                        lockTarget = getTarget(args[2]);
                    } else {
                        sender.sendMessage("Usage: /customitem lock <recipe> [player]");
                        return true;
                    }

                    if (!lockItem.isHasRecipe()) {
                        sender.sendMessage("This item dont have a recipe attached");
                        return true;
                    }

                    if (lockTarget.hasDiscoveredRecipe(lockItem.getKey()))
                        plugin.getItemManager().lockRecipe(lockTarget, lockItem);
                    else
                        sender.sendMessage(lockTarget.getName() + " have not unlock this recipe yet");

                } else {
                    sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
                break;
            // /customitems give <item> [player] [amount]
            case "give":
                if (sender.hasPermission(Permission.CUSTOMITEMS_GIVE.getPerm())) {
                    Player giveTarget;
                    Item giveItem;
                    int amount = 1;

                    if (args.length == 2) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        giveItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            giveTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <item>");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        giveItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (getTarget(args[2]) == null) {
                            sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
                            return true;
                        }

                        giveTarget = getTarget(args[2]);
                    } else if (args.length == 4) {
                        if (plugin.getItemManager().getCustomItem(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ITEM.getChatMessage().replace("{item}", args[1]));
                            return true;
                        }

                        giveItem = plugin.getItemManager().getCustomItem(args[1]);

                        if (getTarget(args[2]) == null) {
                            sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
                            return true;
                        }

                        giveTarget = getTarget(args[2]);

                        amount = Math.max(Methods.getInteger(args[3]), amount);
                    } else {
                        sender.sendMessage("Usage: /customitem give <item> [player] [amount]");
                        return true;
                    }

                    plugin.getItemManager().giveItem(giveTarget, giveItem, amount);
                } else {
                    sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
                break;
            // /customitems giveset <set> [player]
            case "giveset":
                if (sender.hasPermission(Permission.CUSTOMITEMS_GIVESET.getPerm())) {
                    Player target;
                    ArmorSet set;

                    if (args.length == 2) {
                        if (plugin.getItemManager().getArmorSetFromID(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ARMORSET.getChatMessage().replace("{set}", args[1]));
                            return true;
                        }

                        set = plugin.getItemManager().getArmorSetFromID(args[1]);

                        if (sender instanceof Player)
                            target = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <set>");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (plugin.getItemManager().getArmorSetFromID(args[1]) == null) {
                            sender.sendMessage(Message.CI_INVALID_ARMORSET.getChatMessage().replace("{set}", args[1]));
                            return true;
                        }

                        set = plugin.getItemManager().getArmorSetFromID(args[1]);

                        if (getTarget(args[2]) == null) {
                            sender.sendMessage(Message.INVALID_PLAYER.getChatMessage());
                            return true;
                        }

                        target = getTarget(args[2]);
                    } else {
                        sender.sendMessage("Usage: /customitem giveset <set> [player]");
                        return true;
                    }

                    plugin.getItemManager().giveSet(target, set);
                } else {
                    sender.sendMessage(Message.NO_PERMISSION.getChatMessage());
                }
                break;
        }
        return true;
    }

    private Player getTarget(String arg) {
        return Bukkit.getPlayer(arg);
    }
}
