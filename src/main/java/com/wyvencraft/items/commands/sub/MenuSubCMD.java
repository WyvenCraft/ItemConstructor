package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.enums.Category;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuSubCMD extends SubCommand {
    private final WyvenItems addon;

    public MenuSubCMD(WyvenItems addon, String name, String permission, int minArgs, boolean playerCommand) {
        super(addon.getPlugin(), name, permission, minArgs, playerCommand);
        this.addon = addon;
    }

    @Override
    protected void handleCommand(CommandSender sender, String[] args) {
        addon.getItemsMenu().open((Player) sender, Category.MAIN_MENU);
    }
}
