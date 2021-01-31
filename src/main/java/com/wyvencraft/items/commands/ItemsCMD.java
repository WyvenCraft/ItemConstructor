package com.wyvencraft.items.commands;


import com.wyvencraft.api.commands.Command;
import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.commands.sub.*;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ItemsCMD extends Command {
    private final WyvenItems addon;

    public ItemsCMD(WyvenItems addon) {
        super(new HelpSubCMD(addon, "help", "wyvencore.items.help", 0),
                new GiveSetSubCMD(addon, "giveset", "wyvencore.items.giveset", 0),
                new GiveSubCMD(addon, "give", "wyvencore.items.giveitem", 0),
                new LockRecipeSubCMD(addon, "lock", "wyvencore.items.lockrecipe", 0),
                new UnlockRecipSubCMD(addon, "unlock", "wyvencore.items.unlockrecipe", 0));
        this.addon = addon;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, org.bukkit.command.@NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player)
                addon.getItemsMenu().open((Player) sender);
            else {
                SubCommand helpCmd = subCommands.get(0);
                helpCmd.command(sender, new String[0]);
            }
            return true;
        }

        Optional<SubCommand> optionalSubCommand = subCommands.stream().filter(subCommand -> {
            if (args.length >= subCommand.getMinArgs()) {
                return args[0].equalsIgnoreCase(subCommand.getName());
            }
            return false;
        }).findFirst();

        if (optionalSubCommand.isPresent()) {
            SubCommand sub = optionalSubCommand.get();

            sub.command(sender, getSubArgs(sub, args));
            return true;
        }

        return false;
    }
}
