package com.wyvencraft.items;

import com.wyvencraft.api.addon.Addon;
import com.wyvencraft.api.integration.IWyvenCore;
import com.wyvencraft.items.commands.ItemsCMD;
import com.wyvencraft.items.commands.ItemsTabCompleter;
import com.wyvencraft.items.commands.sub.*;
import com.wyvencraft.items.managers.ItemManager;
import com.wyvencraft.items.managers.RecipeManager;
import com.wyvencraft.items.menus.ItemsMenu;
import com.wyvencraft.items.orbs.OrbListener;
import com.wyvencraft.items.orbs.OrbManager;
import com.wyvencraft.items.utils.Message;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class WyvenItems extends Addon implements Listener {

    public static WyvenItems instance;
    public static NamespacedKey WYVEN_ITEM;
    public static NamespacedKey ITEM_TYPE;

    private ItemsMenu itemsMenu;
    private final ItemManager itemManager;
    private final OrbManager orbManager;
    private final RecipeManager recipeManager;

    public WyvenItems(IWyvenCore plugin) {
        super(plugin);
        instance = this;
        WYVEN_ITEM = new NamespacedKey(this.getPlugin().getPlugin(), "wyvenitems");
        ITEM_TYPE = new NamespacedKey(this.getPlugin().getPlugin(), "type");
        recipeManager = new RecipeManager(this, getPlugin().getLanguageManager());
        itemManager = new ItemManager(this, getPlugin().getLanguageManager(), recipeManager);
        orbManager = new OrbManager(this);
    }

    @Override
    public void onLoad() {
        getConfigurationManager().saveDefault("items.yml");
        loadMessages();

        itemManager.loadItems();
    }

    @Override
    public void onEnable() {
        getPlugin().registerCommand("wyvenitems", new ItemsCMD(
                        new MenuSubCMD(this, "open", "wyvencore.items.open", 0, true),
                        new HelpSubCMD(this, "help", "wyvencore.items.help", 0, false),
                        new GiveSetSubCMD(this, "giveset", "wyvencore.items.giveset", 0, false),
                        new GiveSubCMD(this, "give", "wyvencore.items.giveitem", 0, false),
                        new LockRecipeSubCMD(this, "lock", "wyvencore.items.lockrecipe", 0, false),
                        new UnlockRecipSubCMD(this, "unlock", "wyvencore.items.unlockrecipe", 0, false)),
                new ItemsTabCompleter(this),
                "Main command to accessing and giving custom items",
                "/wyvenitems <argument>",
                "witem", "witems", "wi");

        registerListener(new OrbListener(itemManager, orbManager));

        checkDependencies();

        itemsMenu = new ItemsMenu(this, getPlugin().getSmartInventory());
        itemsMenu.loadItemMenus();
    }

    @Override
    public void onDisable() {
        orbManager.clearAllOrbs();
    }

    @Override
    public void reloadConfig() {
        getConfigurationManager().reload("config.yml");
    }

    private HeadDatabaseAPI headDatabase;

    private void checkDependencies() {
        // HeadDatabaseAPI
        if (checkDependency("HeadDatabase", false)) {
            registerListener(this);
        }
    }

    @EventHandler
    public void onHeadDatabaseLoad(DatabaseLoadEvent e) {
        headDatabase = new HeadDatabaseAPI();
    }

    public ItemStack getHead(String owner) {
        try {
            return headDatabase.getItemHead(owner);
        } catch (NullPointerException e) {
            getLogger().info("Could not find the head you were looking for");
            return null;
        }
    }

    private void loadMessages() {
        final FileConfiguration langFile = getPlugin().getConfig("language/en_us.yml");

        for (Message msg : Message.values()) {
            if (!langFile.isSet(msg.getPath()))
                if (msg.getDefaultMessage().length > 1) langFile.set(msg.getPath(), msg.getDefaultMessage());
                else langFile.set(msg.getPath(), msg.getDefaultMessage()[0]);
        }

        getPlugin().saveConfig("language/en_us.yml");
    }

    public boolean isHookEnabled(String plugin) {
        return checkDependency(plugin, true);
    }

    public ItemManager getItemManager() {
        return itemManager;
    }

    public ItemsMenu getItemsMenu() {
        return itemsMenu;
    }

    public OrbManager getOrbManager() {
        return orbManager;
    }

    public RecipeManager getRecipeManager() {
        return recipeManager;
    }
}
