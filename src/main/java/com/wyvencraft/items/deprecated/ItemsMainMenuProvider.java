package com.wyvencraft.items.deprecated;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.enums.Category;
import com.wyvencraft.items.menus.GuiItems;
import com.wyvencraft.smartinventory.*;
import com.wyvencraft.smartinventory.util.SlotPos;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.stream.Collectors;

public class ItemsMainMenuProvider implements InventoryProvider {

    public enum CategoryItem {
        COMBAT(ItemStackBuilder.from(Material.GOLDEN_SWORD).setName("&eCombat", true), Category.COMBAT, SlotPos.of(1, 0)),
        ARMOR(ItemStackBuilder.from(Material.IRON_CHESTPLATE).setName("&eArmor", true), Category.ARMOR, SlotPos.of(2, 0)),
        TOOLS(ItemStackBuilder.from(Material.IRON_AXE).setName("&eTools", true), Category.TOOLS, SlotPos.of(3, 0)),
        MISCELLANEOUS(ItemStackBuilder.from(Material.QUARTZ).setName("&eMiscellaneous", true), Category.MISCELLANEOUS, SlotPos.of(4, 0));

        final ItemStack itemStack;
        final SlotPos slotPos;
        final Category type;

        CategoryItem(ItemStackBuilder builder, Category type, SlotPos slotPos) {
            this.itemStack = builder.addFlag(ItemFlag.HIDE_ATTRIBUTES).getItemStack();
            this.slotPos = slotPos;
            this.type = type;
        }

        public Icon getIcon() {
            return Icon.from(itemStack);
        }
    }

    private final HashMap<UUID, CategoryItem> selected = new HashMap<>();

    @Override
    public void init(@NotNull InventoryContents contents) {
        contents.fill(Icon.cancel(GuiItems.BORDER_ITEM.itemStack));
        contents.fillSquare(SlotPos.of(1, 2), SlotPos.of(4, 7), Icon.EMPTY);

        final Player player = contents.player();

        final CategoryItem selectedCategory = selected.getOrDefault(player.getUniqueId(), null);

        for (CategoryItem category : CategoryItem.values()) {
            if (category.slotPos == null) continue;

            if (selectedCategory != null && category == selectedCategory) {
                List<Item> filtered = setSelected(contents, player, category);
                update(contents, filtered);

                continue;
            }

            contents.set(category.slotPos, category.getIcon().whenClick(e -> {
                if (selected.containsKey(player.getUniqueId())) {
                    CategoryItem select = selected.get(player.getUniqueId());
                    e.contents().set(select.slotPos, contents.get(category.slotPos).get());
                }

                selected.put(player.getUniqueId(), category);
                List<Item> filtered = setSelected(contents, player, category);

                update(contents, filtered);
            }));
        }

        update(contents, WyvenItems.instance.getItemManager().customItems);
    }

    public void update(@NotNull InventoryContents contents, List<Item> filter) {
        final Pagination pagination = contents.pagination();

        final Player player = contents.player();

        if (!player.hasPermission("wyvencore.items.giveitem")) {
            filter = filter.stream().filter(Item::hasRecipe).collect(Collectors.toList());
        }

        final Icon[] icons = new Icon[filter.size()];
        for (int i = 0; i < icons.length; i++) {
            final Item cItem = filter.get(i);

            icons[i] = Icon.click(cItem.getItem(), e -> {

                if (e.click().isRightClick()) {
                    if (player.hasPermission("wyvencore.items.giveitem"))
                        WyvenItems.instance.getItemManager().giveItem(contents.player(), cItem, 1);
                    else if (cItem.hasRecipe()) {
                        // View recipe in a new menu
                        player.sendMessage("item have a recipe: rightclicked");
                    }
                } else if (e.click().isLeftClick()) {
                    if (cItem.hasRecipe()) {
                        // View recipe in a new menu
                        player.sendMessage("item have a recipe: leftclicked");
                    }
                }
            });
        }

        pagination.setIcons(icons);
        pagination.setIconsPerPage(20);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 3)
                .allowOverride(true)
                .blacklist(1, 8).blacklist(2, 8).blacklist(3, 8).blacklist(4, 8)
                .blacklist(2, 0).blacklist(2, 1).blacklist(2, 2)
                .blacklist(3, 0).blacklist(3, 1).blacklist(3, 2)
                .blacklist(4, 0).blacklist(4, 1).blacklist(4, 2));

        if (!pagination.isFirst())
            contents.set(SlotPos.of(5, 6), Icon.click(GuiItems.PREVIOUS_PAGE_ITEM.itemStack, clickEvent -> contents.page().open(contents.player(), pagination.previous().getPage())));
        if (!pagination.isLast())
            contents.set(SlotPos.of(5, 7), Icon.click(GuiItems.NEXT_PAGE_ITEM.itemStack, clickEvent -> contents.page().open(contents.player(), pagination.next().getPage())));
    }

    private List<Item> setSelected(InventoryContents contents, Player player, CategoryItem category) {
        final ItemStack glowingCategory = ItemStackBuilder.from(category.itemStack.clone())
                .addGlowEffect()
                .getItemStack();

        contents.set(category.slotPos, Icon.click(glowingCategory, e1 -> {
            selected.remove(player.getUniqueId());
            init(contents);
        }));

        // Filter items to the correct category
        return WyvenItems.instance.getItemManager().customItems.stream()
                .filter(i -> i.getType().getCategory() == category.type)
                .collect(Collectors.toList());
    }
}
