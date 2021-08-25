package com.wyvencraft.items.menus.categories;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.menus.GuiItems;
import com.wyvencraft.smartinventory.*;
import com.wyvencraft.smartinventory.util.SlotPos;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemMenu implements InventoryProvider {
    //    private final Category category;
    private final List<Item> itemList;

    protected ItemMenu(List<Item> itemList) {
//        this.category = category;
        this.itemList = itemList;
    }

    @Override
    public void init(@NotNull InventoryContents contents) {
        InventoryProvider.super.init(contents);

        contents.fill(Icon.cancel(GuiItems.BORDER_ITEM.itemStack));
        contents.fillSquare(SlotPos.of(1, 2), SlotPos.of(4, 7), Icon.EMPTY);

        contents.page().parent().ifPresent(parent -> {
            contents.set(GuiItems.BACK_ITEM.slotPos, Icon.click(GuiItems.BACK_ITEM.itemStack, e -> parent.open(contents.player())));
        });
    }

    public void update(@NotNull InventoryContents contents, List<Item> items) {
        final Pagination pagination = contents.pagination();

        final Player player = contents.player();

        List<Item> filter = new ArrayList<>(items);

        if (!player.hasPermission("wyvencore.items.giveitem")) {
            filter = items.stream().filter(Item::hasRecipe).collect(Collectors.toList());
        }

        final Icon[] icons = new Icon[filter.size()];

        for (int i = 0; i < icons.length; i++) {
            final Item cItem = filter.get(i);

            icons[i] = Icon.click(cItem.getItem(), e -> {

                if (e.click().isRightClick()) {
                    if (player.hasPermission("wyvencore.items.giveitem"))
                        WyvenItems.instance.getItemManager().giveItem(player, cItem, 1);
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
        pagination.setIconsPerPage(24);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 2)
                .allowOverride(true)
                .blacklist(1, 8).blacklist(2, 8).blacklist(3, 8) // Back border
                .blacklist(2, 0).blacklist(3, 0).blacklist(4, 0)
                .blacklist(2, 1).blacklist(3, 1).blacklist(4, 1));

        if (!pagination.isFirst())
            contents.set(SlotPos.of(5, 6), Icon.click(GuiItems.PREVIOUS_PAGE_ITEM.itemStack, clickEvent -> contents.page().open(contents.player(), pagination.previous().getPage())));
        if (!pagination.isLast())
            contents.set(SlotPos.of(5, 7), Icon.click(GuiItems.NEXT_PAGE_ITEM.itemStack, clickEvent -> contents.page().open(contents.player(), pagination.next().getPage())));
    }

    public List<Item> getItemList() {
        return itemList;
    }
}
