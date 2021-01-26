package com.wyvencraft.items.commands;


import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.Item;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.recipes.Recipe;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ItemsTabCompleter implements TabCompleter {
    List<String> arguments = new ArrayList<>();
    List<String> items = new ArrayList<>();
    List<String> recipes = new ArrayList<>();

    WyvenItems addon;
    WyvenAPI plugin;
    public ItemsTabCompleter(WyvenItems addon) {
        this.addon = addon;
        plugin = addon.getPlugin();
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String alias, String[] args) {
        if (arguments.isEmpty()) {
            arguments.add("help");
            arguments.add("give");
            arguments.add("giveset");
            arguments.add("unlock");
            arguments.add("lock");
        }

        if (items.isEmpty()) {
            for (Item item : addon.getItemManager().customItems) {
                items.add(item.getName());
            }
        }

        if (recipes.isEmpty()) {
            for (Recipe recipe : Recipe.customRecipes.values()) {
                recipes.add(recipe.customItem);
            }
        }

        if (args.length == 1) {
            if (sender.hasPermission("wyvencore.items.help")) {
                List<String> result = new ArrayList<>();
                for (String a : arguments) {
                    if (a.toLowerCase().startsWith(args[0].toLowerCase())) {
                        result.add(a);
                    }
                }
                return result;
            }
        }

        switch (args[0].toLowerCase()) {
            // /customitems give(0) <item>(1) [player](2) [amount](3)
            case "give":
                if (sender.hasPermission("wyvencore.items.give")) {
                    if (args.length == 2) {
                        List<String> result = new ArrayList<>();
                        for (String a : items) {
                            if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                                result.add(a);
                            }
                        }
                        return result;
                    }

                    if (args.length == 3) {
                        List<String> result = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                result.add(player.getName());
                            }
                        }
                        return result;
                    }
                    if (args.length == 4) {
                        List<String> result = new ArrayList<>();
                        result.add("1");
                        result.add("2");
                        result.add("4");
                        result.add("8");
                        result.add("16");
                        result.add("32");
                        result.add("64");
                        return result;
                    }
                }
                break;
            // /customitems unlock(0) <recipe>(1) [player](2)
            case "unlock":
                if (sender.hasPermission("wyvencore.items.unlockrecipe")) {
                    if (args.length == 2) {
                        List<String> result = new ArrayList<>();
                        for (String a : recipes) {
                            if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                                result.add(a);
                            }
                        }
                        return result;
                    }

                    if (args.length == 3) {
                        List<String> result = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                result.add(player.getName());
                            }
                        }
                        return result;
                    }
                }
                break;
            case "lock":
                if (sender.hasPermission("wyvencore.items.lockrecipe")) {
                    if (args.length == 2) {
                        List<String> result = new ArrayList<>();
                        for (String a : recipes) {
                            if (a.toLowerCase().startsWith(args[1].toLowerCase())) {
                                result.add(a);
                            }
                        }
                        return result;
                    }

                    if (args.length == 3) {
                        List<String> result = new ArrayList<>();
                        for (Player player : Bukkit.getOnlinePlayers()) {
                            if (player.getName().toLowerCase().startsWith(args[2].toLowerCase())) {
                                result.add(player.getName());
                            }
                        }
                        return result;
                    }
                }
                break;
        }

        return null;
    }
}
