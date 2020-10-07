package com.wyvencraft.items;

import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.common.ItemBuilder;
import com.wyvencraft.wyvencore.common.Lang;
import com.wyvencraft.wyvencore.menus.GuiItem;
import com.wyvencraft.wyvencore.menus.menuscreators.CraftingMenu;
import com.wyvencraft.wyvencore.player.PlayerStats;
import com.wyvencraft.wyvencore.utils.CmdAction;
import com.wyvencraft.wyvencore.utils.Debug;
import io.github.portlek.smartinventory.Icon;
import io.github.portlek.smartinventory.InventoryContents;
import io.github.portlek.smartinventory.InventoryProvided;
import io.github.portlek.smartinventory.util.SlotPos;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CraftingTableProvider implements InventoryProvided {

    // TODO: update craftables if player drops an item

    private final List<GuiItem> guiItems;
    private final ItemStack invalidRecipe;
    private final ItemStack lockedRecipe;
    private final ItemStack recipesSlot;

    public CraftingTableProvider(List<GuiItem> _guiItems,
                                 ItemStack _invalidRecipe,
                                 ItemStack _lockedRecipe,
                                 ItemStack _recipesSlot) {
        this.guiItems = _guiItems;
        this.invalidRecipe = _invalidRecipe;
        this.lockedRecipe = _lockedRecipe;
        this.recipesSlot = _recipesSlot;
    }

    @Override
    public void init(@NotNull InventoryContents contents) {
        PlayerStats ps = Core.instance.getStatsManager().getPlayerStats(contents.player().getUniqueId());
        for (GuiItem guiItem : guiItems) {
            ItemStack item = updateItem(ps, guiItem.getItem());

            if (guiItem.isFill()) {
                contents.fill(Icon.cancel(item));
                continue;
            }

            if (guiItem.getSlot() != null) {
                switch (guiItem.getType()) {
                    case PLACEHOLDER:
                    case PREVIOUS_PAGE:
                    case NEXT_PAGE:
                        if (guiItem.getActions() != null) {
                            contents.set(guiItem.getSlot(), Icon.click(item, clickEvent -> {
                                for (String action : guiItem.getActions()) {
                                    String[] split = action.split("] ", 2);
                                    CmdAction.execute(ps, split[0], split[1]);
                                }
                            }));
                        } else {
                            contents.set(guiItem.getSlot(), Icon.cancel(item));
                        }
                        break;
                    case CLOSE:
                        contents.set(guiItem.getSlot(), Icon.click(item, clickEvent -> contents.page().close(contents.player())));
                        break;
                    case BACK:
                        contents.set(guiItem.getSlot(), Icon.click(item, clickEvent -> contents.page().parent().get().open(contents.player())));
                        break;
                }
                continue;
            }

            if (guiItem.getSlots() != null) {
                for (SlotPos slot : guiItem.getSlots()) {
                    switch (guiItem.getType()) {
                        case PLACEHOLDER:
                        case PREVIOUS_PAGE:
                        case NEXT_PAGE:
                            if (guiItem.getActions() != null) {
                                contents.set(slot, Icon.click(item, clickEvent -> {
                                    for (String action : guiItem.getActions()) {
                                        String[] split = action.split("] ", 2);
                                        CmdAction.execute(ps, split[0], split[1]);
                                    }
                                }));
                            } else {
                                contents.set(slot, Icon.cancel(item));
                            }
                            break;
                        case CLOSE:
                            contents.set(slot, Icon.click(item, clickEvent -> contents.page().close(contents.player())));
                            break;
                        case BACK:
                            contents.set(slot, Icon.click(item, clickEvent -> {
                                if (contents.page().parent().isPresent())
                                    contents.page().parent().get().open(contents.player());
                                else contents.player().sendMessage(Lang.color("&cParent page could not be found!"));
                            }));
                            break;
                    }
                }
            }
        }

        // CRAFTING GRID
        for (SlotPos slot : CraftingMenu.instance.craftingSlots) {
            contents.set(slot, Icon.empty()).setEditable(slot, true);
        }

        contents.set(CraftingMenu.instance.resultSlot, Icon.cancel(invalidRecipe));
        //--------------------------

//        findCraftables(contents, ps);
        fillCraftableItems(contents, ps);
    }

    public List<Recipe> canCraftItems(InventoryContents contents, PlayerStats ps) {
        Inventory pInv = contents.getBottomInventory();
        List<ItemStack> checked = new ArrayList<>();

        List<Recipe> craftables = new ArrayList<>();
        if (pInv.getContents().length > 0) {
            ItemStack[] items = Stream.of(pInv.getContents())
                    .filter(Objects::nonNull)
                    .toArray(ItemStack[]::new);

            for (ItemStack item : items) {
                if (checked.contains(item.asOne())) continue;
                checked.add(item.asOne());

                for (Recipe recipe : Recipe.customRecipes.values().stream()
                        .filter(r -> r.recipe.values().stream()
                                .allMatch(ingred -> ingred.isSimilar(item)))
                        .collect(Collectors.toList())) {

                    if (ps.hasUnlockedRecipe(recipe) || contents.player().isOp()) {
                        if (recipe.canCraft(pInv)) {
                            if (!craftables.contains(recipe)) {
                                craftables.add(recipe);
                            }
                        }
                    }
                }
            }
        }
        return craftables;
    }

    public void fillCraftableItems(InventoryContents contents, PlayerStats ps) {
        for (int i = 0; i < CraftingMenu.instance.recipesSlots.length; i++) {
            SlotPos slot = CraftingMenu.instance.recipesSlots[i];
            try {
                Recipe recipe = canCraftItems(contents, ps).get(i);
                contents.set(slot, Icon.click(recipe.result.asOne(), clickEvent -> {
                    Debug.log("auto craft: " + clickEvent.current().get().getI18NDisplayName());
                    craft(contents, ps, recipe, true);
                    if (clickEvent.cursor().isPresent()) {
                        ItemStack stack = clickEvent.current().get();
                        if (stack.isSimilar(recipe.result) && stack.getAmount() < 64) {
                            clickEvent.cursor().get().asQuantity(stack.getAmount() + 1);
                        }
                    }
                }));
            } catch (IndexOutOfBoundsException e) {
                contents.set(slot, Icon.cancel(recipesSlot));
            }
        }
    }

    private Map<Integer, ItemStack> getGrid(InventoryContents contents) {
        Inventory workbench = contents.getTopInventory();

        Map<Integer, ItemStack> grid = new HashMap<>();
        for (SlotPos pos : CraftingMenu.instance.craftingSlots) {
            int slot = pos.getRow() * 9 + pos.getColumn();
            if (workbench.getItem(slot) != null) {
                grid.put(slot, workbench.getItem(slot));
//                Debug.log(slot + " " + workbench.getItem(slot).getI18NDisplayName());
            }
        }

        Debug.log("returned grid");
        return grid;
    }

    private void craft(InventoryContents contents, PlayerStats ps, Recipe recipe, boolean auto) {
        Inventory workbench = contents.getTopInventory();

        if (auto) {
            for (ItemStack ingred : recipe.recipe.values()) {
                contents.player().getInventory().removeItem(ingred);
            }
        } else {
            for (int i = 0; i < 9; i++) {
                if (recipe.recipe.containsKey(i + 1)) {
                    SlotPos pos = CraftingMenu.instance.craftingSlots[i];
                    int slot = pos.getRow() * 9 + pos.getColumn();

                    ItemStack item = workbench.getItem(slot);

                    if (item != null)
                        item.setAmount(item.getAmount() - recipe.recipe.get(i + 1).getAmount());
                }
            }
        }

        if (!recipe.canCraft(contents.player().getInventory())) {
            fillCraftableItems(contents, ps);
        }
    }

    private Recipe getRecipe(InventoryContents contents) {
        Map<Integer, ItemStack> grid = getGrid(contents);
        for (Recipe recipe : Recipe.customRecipes.values()) {
            if (recipe.matches(grid)) {
                return recipe;
            }
        }
        return null;
    }

    @Override
    public void update(@NotNull InventoryContents contents) {
        PlayerStats ps = Core.instance.getStatsManager().getPlayerStats(contents.player().getUniqueId());

        Recipe gridRecipe = getRecipe(contents);

        if (gridRecipe == null) {
            contents.set(CraftingMenu.instance.resultSlot, Icon.cancel(invalidRecipe));
            return;
        }

        Debug.log("final recipe: " + gridRecipe.customItem);

        if (ps.hasUnlockedRecipe(gridRecipe) || contents.player().isOp()) {
            Debug.log("unlocked");
            contents.set(CraftingMenu.instance.resultSlot, Icon.click(gridRecipe.result.asOne(), clickEvent -> {
                craft(contents, ps, gridRecipe, false);
                clickEvent.cursor().get().setAmount(gridRecipe.result.getAmount());


                contents.notifyUpdate();
            }));
        } else {
            Debug.log("locked");
            contents.set(CraftingMenu.instance.resultSlot, Icon.cancel(lockedRecipe));
        }

    }

    private ItemStack updateItem(PlayerStats ps, ItemBuilder builder) {

        ItemStack item = builder.build();
        ItemMeta meta = item.getItemMeta();

        List<String> lore = new ArrayList<>();

        for (int i = 0; i < builder.getLore().size(); i++) {
            String line = builder.getLore().get(i);
            for (Map.Entry<String, String> replace : Core.instance.getStatsManager().getReplacements(ps, true, true, true, true).entrySet()) {
                line = line.replace(replace.getKey(), replace.getValue());
            }

            line = PlaceholderAPI.setPlaceholders((OfflinePlayer) ps.getPlayer(), line);
            lore.add(line);
        }
        meta.setLore(lore);

        String displayname = builder.getDisplayName();
        for (Map.Entry<String, String> replace : Core.instance.getStatsManager().getReplacements(ps, true, true, true, true).entrySet()) {
            displayname = displayname.replace(replace.getKey(), replace.getValue());
        }
        displayname = PlaceholderAPI.setPlaceholders((OfflinePlayer) ps.getPlayer(), displayname);
        meta.setDisplayName(Lang.color(displayname));

        if (builder.getMaterial() == Material.PLAYER_HEAD && builder.getHead().startsWith("player-")) {
            builder.withSkull(builder.getHead().replace("{player_name}", ps.getPlayer().getUniqueId().toString()));
        }

        item.setItemMeta(meta);

        return item;
    }
}
