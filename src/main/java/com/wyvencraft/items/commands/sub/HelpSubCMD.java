package com.wyvencraft.items.commands.sub;

import com.wyvencraft.api.commands.SubCommand;
import com.wyvencraft.api.managers.ILangManager;
import com.wyvencraft.items.WyvenItems;
import org.bukkit.command.CommandSender;

import java.util.regex.Pattern;

public class HelpSubCMD extends SubCommand {
    public HelpSubCMD(WyvenItems addon, String name, String permission, int minArgs) {
        super(addon.getPlugin(), name, permission, minArgs);
    }

    @Override
    protected void handleCommand(CommandSender sender, String[] strings) {
        final ILangManager lang = getPlugin().getLangManager();
        final String helpMessage = lang.getMessage("ITEMS.HELP");
        String[] message = helpMessage.split(Pattern.quote("\n"));
        lang.sendMessage(sender, message);
    }
}
