package com.wyvencraft.items.managers;

import com.wyvencraft.api.language.LanguageManager;
import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.data.ItemRecipe;
import com.wyvencraft.items.utils.Message;
import com.wyvencraft.items.utils.Utils;
import com.wyvencraft.items.utils.Validator;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public final class RecipeManager {
    private final WyvenItems addon;
    private final LanguageManager lang;

    public RecipeManager(WyvenItems addon, LanguageManager lang) {
        this.addon = addon;
        this.lang = lang;
    }

    public ItemRecipe createRecipe(ConfigurationSection recipeSection, ItemStack result) {
        Map<Integer, ItemStack> recipe = new HashMap<>();

        for (String raw : recipeSection.getKeys(false)) {
            final int slot = Utils.getInteger(raw);
            if (slot == -1 || slot > 8) {
                addon.getLogger().severe(raw + " has to be a number (0-8).");
                return null;
            }

            final String[] ingredStr = recipeSection.getString(raw).split(";", 2);

            int amount = 1;
            if (ingredStr.length > 1) {
                int input = Utils.getInteger(ingredStr[1]);
                amount = input != -1 ? input : amount;
            }

            final String material = ingredStr[0];

            final ItemStack ingredient = Validator.validateItem(material, amount);
            if (ingredient == null) return null;

            recipe.put(slot, ingredient);
        }

        return new ItemRecipe(recipe, ItemRecipe.ShapeType.SHAPED, result);
    }

    public void unlockRecipe(Player p, Item item) {
        if (!p.discoverRecipe(item.getKey())) {
            lang.sendMessage(p, Message.ALREADY_UNLOCKED_MESSAGE.getPath(),
                    r -> r.replace("{0}", p.getName()).replace("{1}", item.getName()));
            return;
        }

        lang.sendMessage(p, Message.UNLOCKED_RECIPE_MESSAGE.getPath(),
                r -> r.replace("{0}", item.getName()).replace("{1}", p.getName()));
    }

    public void lockRecipe(Player p, Item item) {
        if (!p.undiscoverRecipe(item.getKey())) {
            lang.sendMessage(p, Message.NOT_UNLOCKED_MESSAGE.getPath(),
                    r -> r.replace("{0}", p.getName()).replace("{1}", item.getName()));
            return;
        }

        lang.sendMessage(p, Message.LOCKED_RECIPE_MESSAGE.getPath(),
                r -> r.replace("{0}", item.getName()).replace("{1}", p.getName()));
    }
}
