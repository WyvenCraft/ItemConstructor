package com.wyvencraft.items;

import com.wyvencraft.api.addon.Addon;
import com.wyvencraft.api.integration.IWyvenCore;
import com.wyvencraft.items.commands.ItemsCMD;
import com.wyvencraft.items.commands.ItemsTabCompleter;
import com.wyvencraft.items.listeners.OrbListener;
import com.wyvencraft.items.managers.ItemManager;
import com.wyvencraft.items.menus.ItemsMenu;
import com.wyvencraft.items.menus.ItemsMenuProvider;
import com.wyvencraft.items.utils.Message;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;

public class WyvenItems extends Addon {

    public static WyvenItems instance;
    private final ItemManager itemManager;

    public static NamespacedKey WYVEN_ITEM;
    public static NamespacedKey ITEM_TYPE;

    private ItemsMenu itemsMenu;

    public WyvenItems(IWyvenCore plugin) {
        super(plugin);
        instance = this;
        WYVEN_ITEM = new NamespacedKey(this.getPlugin().getPlugin(), "wyvenitems");
        ITEM_TYPE = new NamespacedKey(this.getPlugin().getPlugin(), "type");
        itemManager = new ItemManager(this);
    }

    @Override
    public void onLoad() {

        getPlugin().printDebug("Load start");
        getConfigurationManager().saveDefault("items.yml");
        loadMessages();

        getPlugin().printDebug("Load mid");
        itemManager.loadItems();
        getPlugin().printDebug("Load end");
    }

    @Override
    public void onEnable() {
        ItemsCMD cmd = new ItemsCMD(this);
        getPlugin().registerCommand("wyvenitems", cmd, new ItemsTabCompleter(this), "Main command to accessing and giving custom items", "/wyvenitems <argument>", "witem", "witems", "wi");

        registerListener(new OrbListener(itemManager));

        itemsMenu = new ItemsMenu(getPlugin().getSmartInventory(), new ItemsMenuProvider());
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void reloadConfig() {
        getConfigurationManager().reload("config.yml");
    }

    private void loadMessages() {
        final FileConfiguration langFile = getPlugin().getConfig("language/en_us.yml");

        for (Message msg : Message.values()) {
            if (!langFile.isSet(msg.getPath()))
                if (msg.getDefaultMessage().length > 1) langFile.set(msg.getPath(), msg.getDefaultMessage());
                else langFile.set(msg.getPath(), msg.getDefaultMessage()[0]);
        }

        getPlugin().saveConfig("language/en_us.yml.yml");
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ItemsMenu getItemsMenu() {
        return itemsMenu;
    }
}
