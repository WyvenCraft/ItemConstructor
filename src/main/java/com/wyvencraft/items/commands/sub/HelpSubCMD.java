package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.integration.WyvenAPI;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.command.CommandSender;

public class HelpSubCMD extends SubCommand {
    private final WyvenItems addon;
    private final WyvenAPI plugin;

    public HelpSubCMD(WyvenAPI plugin, WyvenItems addon, String name, String permission, int minArgs) {
        super(plugin, name, permission, minArgs);
        this.addon = addon;
        this.plugin = plugin;
    }

    @Override
    protected void handleCommand(CommandSender sender, String[] strings) {
        plugin.getLangManager().sendMessage(sender, "help");
    }
}
