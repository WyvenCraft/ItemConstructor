package com.wyvencraft.items.commands;


import com.wyvencraft.api.commands.Command;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.commands.sub.*;

public class ItemsCMD extends Command {
    public ItemsCMD(WyvenItems addon) {
        new GiveSetSubCMD(addon.getPlugin(), addon, "giveset","wyvencore.items.giveset", 1);
        new GiveSubCMD(addon.getPlugin(), addon, "give","wyvencore.items.giveitem", 1);
        new HelpSubCMD(addon.getPlugin(), addon, "help","wyvencore.items.help", 0);
        new LockRecipeSubCMD(addon.getPlugin(), addon, "lock","wyvencore.items.lockrecipe", 1);
        new UnlockRecipSubCMD(addon.getPlugin(), addon, "unlock","wyvencore.items.unlockrecipe", 1);
    }
}
