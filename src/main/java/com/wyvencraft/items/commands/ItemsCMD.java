package com.wyvencraft.items.commands;


import com.wyvencraft.api.commands.Command;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.commands.sub.*;

public class ItemsCMD extends Command {
    public ItemsCMD(WyvenItems addon) {
        super(new HelpSubCMD(addon, "help", "wyvencore.items.help", 0),
                new GiveSetSubCMD(addon, "giveset", "wyvencore.items.giveset", 1),
                new GiveSubCMD(addon, "give", "wyvencore.items.giveitem", 1),
                new LockRecipeSubCMD(addon, "lock", "wyvencore.items.lockrecipe", 1),
                new UnlockRecipSubCMD(addon, "unlock", "wyvencore.items.unlockrecipe", 1));
    }
}
