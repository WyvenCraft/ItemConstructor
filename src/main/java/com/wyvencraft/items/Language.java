package com.wyvencraft.items;

import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;

public class Language {
    WyvenItems addon;

    public Language(WyvenItems addon) {
        this.addon = addon;
    }

    public void initLang() {
        final FileConfiguration lang = addon.getPlugin().getConfig(addon.getPlugin().getLangManager().getLanguage() + ".yml");

        if (!lang.isSet("ITEMS.HELP")) lang.set("ITEMS.HELP", new ArrayList<String>() {
            {
                add("/witems help");
                add("/witems give <item> [player] [amount]");
                add("/witems giveset <set> [player]");
                add("/witems lock <item> [player]");
                add("/witems unlock <item> [player]");
            }
        });
        // {0} = invalid item displayname
        if (!lang.isSet("ITEMS.INVALID_ITEM")) lang.set("ITEMS.INVALID_ITEM", "&cInvalid item: &7{0}");
        // {0} = invalid set displayname
        if (!lang.isSet("ITEMS.INVALID_ARMOR_SET")) lang.set("ITEMS.INVALID_ARMOR_SET", "&cInvalid armor set: &7{0}");
        // {0} = item Displayname
        if (!lang.isSet("ITEMS.NO_RECIPE")) lang.set("ITEMS.NO_RECIPE", "{0} have no recipes attached");
        // {0} = target player - {1} = item Displayname
        if (!lang.isSet("ITEMS.ALREADY_UNLOCKED"))
            lang.set("ITEMS.ALREADY_UNLOCKED", "{0} has already unlocked {1} recipe");
        // {0} = target player - {1} = item Displayname
        if (!lang.isSet("ITEMS.NOT_UNLOCKED")) lang.set("ITEMS.NOT_UNLOCKED", "{0} have not unlocked recipe for {1}");
        // {0} = amount - {1} = item Displayname
        if (!lang.isSet("ITEMS.RECEIVED_ITEM")) lang.set("ITEMS.RECEIVED_ITEM", "&a&l+ {0} &7{1}");
        // {0} = target player - {1} = item Displayname - {2} = amount
        if (!lang.isSet("ITEMS.GAVE_ITEM")) lang.set("ITEMS.GAVE_ITEM", "&7You gave &c{0} &7{1} (&cx{2}&7)");
        // {0} = target player - {1} = set Displayname
        if (!lang.isSet("ITEMS.GAVE_ARMOR_SET")) lang.set("ITEMS.GAVE_ARMOR_SET", "&7You gave &c{0} &7{1}");
        // {0} = item displayname
        if (!lang.isSet("ITEMS.UNLOCKED_RECIPE")) lang.set("ITEMS.UNLOCKED_RECIPE", "&a&lUNLOCKED &7recipe for {0}");
        // {0} = item displayname
        if (!lang.isSet("ITEMS.LOCKED_RECIPE"))
            lang.set("ITEMS.LOCKED_RECIPE", "&c&lLOCKED &7recipe for {0} (lost access to this recipe)");

        addon.getPlugin().saveConfig(addon.getPlugin().getLangManager().getLanguage() + ".yml");
    }

}
