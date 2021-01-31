package com.wyvencraft.items.utils;

public enum Message {
    HELP_MESSAGE("ITEMS.HELP",
            "/witems help",
            "/witems give <item> [player] [amount]",
            "/witems giveset <set> [player]",
            "/witems lock <item> [player]",
            "/witems unlock <item> [player]"),
    // {0} = invalid item displayname
    INVALID_ITEM_MESSAGE("ITEMS.INVALID_ITEM", "&cInvalid item &7{0}&c."),
    // {0} = invalid set displayname
    INVALID_ARMOR_SET_MESSAGE("ITEMS.INVALID_ARMOR_SET", "&cInvalid armor set &7{0}&c."),
    // {0} = item Displayname
    NO_RECIPE_MESSAGE("ITEMS.NO_RECIPE", "&7{0} &chave no recipes attached."),
    // {0} = target player - {1} = item Displayname
    ALREADY_UNLOCKED_MESSAGE("ITEMS.ALREADY_UNLOCKED", "&7{0} &chas already unlocked &7{1} &crecipe."),
    // {0} = target player - {1} = item Displayname
    NOT_UNLOCKED_MESSAGE("ITEMS.NOT_UNLOCKED", "&7{0} &chave not unlocked recipe for &7{1}&c."),
    // {0} = amount - {1} = item Displayname
    RECEIVED_ITEM_MESSAGE("ITEMS.RECEIVED_ITEM", "&a&l+ {0} &7{1}"),
    // {0} = target player - {1} = item Displayname - {2} = amount
    GAVE_ITEM_MESSAGE("ITEMS.GAVE_ITEM", "&7You gave &c{0} &7{1} (&cx{2}&7)"),
    // {0} = target player - {1} = set Displayname
    GAVE_ARMOR_SET_MESSAGE("ITEMS.GAVE_ARMOR_SET", "&7You gave &c{0} &a{1} &7set"),
    // {0} = item displayname - {1} target player
    UNLOCKED_RECIPE_MESSAGE("ITEMS.UNLOCKED_RECIPE", "&a&lUNLOCKED &7recipe for {0}"),
    // {0} = item displayname - {1} target player
    LOCKED_RECIPE_MESSAGE("ITEMS.LOCKED_RECIPE", "&c&lLOCKED &7recipe for {0} (lost access to this recipe)");

    final String path;
    final String[] defaultMessage;

    Message(String path, String... defaultMessage) {
        this.path = path;
        this.defaultMessage = defaultMessage;
    }

    public String[] getDefaultMessage() {
        return defaultMessage;
    }

    public String getPath() {
        return path;
    }
}
