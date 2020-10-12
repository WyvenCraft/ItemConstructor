package com.wyvencraft.items.commands;

import com.wyvencraft.WyvenCore;
import com.wyvencraft.interfaces.ILanguageManager;
import com.wyvencraft.interfaces.IWyvenCore;
import com.wyvencraft.items.ArmorSet;
import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ItemsCMD implements CommandExecutor {

    WyvenItems addon;
    IWyvenCore plugin;

    public ItemsCMD(WyvenItems addon) {
        this.addon = addon;
        this.plugin = addon.getPlugin();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, String[] args) {
        if (args.length == 0) {
            if (checkNoPermission(sender, "wyvencore.items.help")) {
                sendMessage(sender, "items.help");
            }

            return true;
        }

        switch (args[0].toLowerCase()) {
            case "help":
                if (checkNoPermission(sender, "wyvencore.items.help")) {
                    sendMessage(sender, "items.help");
                }
                break;
            // /customitem unlock <recipe> [player]
            case "unlock":
                if (checkNoPermission(sender, "wyvencore.items.unlockrecipe")) {
                    Player unlockTarget;
                    Item unlockItem;
                    if (args.length == 2) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        unlockItem = addon.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            unlockTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <recipe>");
                            return true;
                        }

                    } else if (args.length == 3) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        unlockItem = addon.getItemManager().getCustomItem(args[1]);

                        unlockTarget = getPlayer(sender, args[2]);
                        if (unlockTarget == null) return true;
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
                        addon.getItemManager().unlockRecipe(unlockTarget, unlockItem);
                    else
                        sender.sendMessage(unlockTarget.getName() + " has already unlocked this recipe");

                }
                break;
            // /customitem unlock <recipe> [player]
            case "lock":
                if (checkNoPermission(sender, "wyvencore.items.lockrecipe")) {
                    Player lockTarget;
                    Item lockItem;
                    if (args.length == 2) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        lockItem = addon.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            lockTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <recipe>");
                            return true;
                        }

                    } else if (args.length == 3) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        lockItem = addon.getItemManager().getCustomItem(args[1]);

                        lockTarget = getPlayer(sender, args[2]);
                        if (lockTarget == null) return true;

                    } else {
                        sender.sendMessage("Usage: /customitem lock <recipe> [player]");
                        return true;
                    }

                    if (!lockItem.isHasRecipe()) {
                        sender.sendMessage("This item dont have a recipe attached");
                        return true;
                    }

                    if (lockTarget.hasDiscoveredRecipe(lockItem.getKey()))
                        addon.getItemManager().lockRecipe(lockTarget, lockItem);
                    else
                        sender.sendMessage(lockTarget.getName() + " have not unlock this recipe yet");

                }
                break;
            // /customitems give <item> [player] [amount]
            case "give":
                if (checkNoPermission(sender, "wyvencore.items.give")) {
                    Player giveTarget;
                    Item giveItem;
                    int amount = 1;

                    if (args.length == 2) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        giveItem = addon.getItemManager().getCustomItem(args[1]);

                        if (sender instanceof Player)
                            giveTarget = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <item>");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        giveItem = addon.getItemManager().getCustomItem(args[1]);

                        giveTarget = getPlayer(sender, args[2]);
                        if (giveTarget == null) return true;

                    } else if (args.length == 4) {
                        if (addon.getItemManager().getCustomItem(args[1]) == null) {
                            sendMessage(sender, "items.invalid_item");
                            return true;
                        }

                        giveItem = addon.getItemManager().getCustomItem(args[1]);

                        giveTarget = getPlayer(sender, args[2]);
                        if (giveTarget == null) return true;

                        amount = Math.max(Methods.getInteger(args[3]), amount);
                    } else {
                        sender.sendMessage("Usage: /customitem give <item> [player] [amount]");
                        return true;
                    }

                    addon.getItemManager().giveItem(giveTarget, giveItem, amount);
                }
                break;
            // /customitems giveset <set> [player]
            case "giveset":
                if (checkNoPermission(sender, "wyvencore.items.giveset")) {
                    Player target;
                    ArmorSet set;

                    if (args.length == 2) {
                        if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                            sendMessage(sender, "items.invalid_armorset");
                            return true;
                        }

                        set = addon.getItemManager().getArmorSetFromID(args[1]);

                        if (sender instanceof Player)
                            target = (Player) sender;
                        else {
                            sender.sendMessage("Please specify player after <set>");
                            return true;
                        }
                    } else if (args.length == 3) {
                        if (addon.getItemManager().getArmorSetFromID(args[1]) == null) {
                            sendMessage(sender, "items.invalid_armorset");
                            return true;
                        }

                        set = addon.getItemManager().getArmorSetFromID(args[1]);

                        target = getPlayer(sender, args[2]);

                        if (target == null) {
                            return true;
                        }

                    } else {
                        sender.sendMessage("Usage: /customitem giveset <set> [player]");
                        return true;
                    }

                    addon.getItemManager().giveSet(target, set);
                }
                break;
        }
        return true;
    }

    private void sendMessage(final CommandSender sender, final String key) {
        final ILanguageManager languageManager = ((WyvenCore) this.plugin).getLanguageManger();
        if (sender instanceof Player) {
            final Player player = (Player) sender;
            languageManager.sendMessage(player, key);
            return;
        }
        String message = languageManager.getMessageColoredWithPrefix(key);

        languageManager.sendMessage(sender, message);
    }

    private boolean checkNoPermission(final CommandSender sender, final String permission) {
        if (sender.hasPermission(permission)) {
            return false;
        }
        this.sendMessage(sender, "no_permission");
        return true;
    }

    private Player getPlayer(final CommandSender sender, final String targetName) {
        final Player player = Bukkit.getPlayer(targetName);
        if (player == null) {
            this.sendMessage(sender, "invalid_target");
            return null;
        }
        return player;
    }
}
