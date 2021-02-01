package com.wyvencraft.items.menus;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.enums.ItemType;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import io.github.portlek.smartinventory.*;
import io.github.portlek.smartinventory.util.SlotPos;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class ItemsMenuProvider implements InventoryProvider {

    public enum GuiItem {
        BORDER_ITEM(ItemStackBuilder.from(Material.GRAY_STAINED_GLASS_PANE).name(" "), ItemType.NULL),
        HELMET_ITEM(ItemStackBuilder.from(Material.DIAMOND_HELMET).name("&eHelmets", true), ItemType.HELMET, SlotPos.of(1, 1)),
        CHESTPLATES_ITEM(ItemStackBuilder.from(Material.DIAMOND_CHESTPLATE).name("&eChestplates", true), ItemType.CHESTPLATE, SlotPos.of(2, 1)),
        LEGGINGS_ITEM(ItemStackBuilder.from(Material.DIAMOND_LEGGINGS).name("&eLeggings", true), ItemType.LEGGING, SlotPos.of(3, 1)),
        BOOTS_ITEM(ItemStackBuilder.from(Material.DIAMOND_BOOTS).name("&eBoots", true), ItemType.BOOTS, SlotPos.of(4, 1)),
        COMBAT_ITEMS_ITEM(ItemStackBuilder.from(Material.DIAMOND_SWORD).name("&eCombat", true), ItemType.COMBAT, SlotPos.of(1, 0)),
        TOOL_ITEMS_ITEM(ItemStackBuilder.from(Material.DIAMOND_PICKAXE).name("&eTools", true), ItemType.TOOL, SlotPos.of(2, 0)),
        ARCHERY_ITEMS_ITEM(ItemStackBuilder.from(Material.BOW).name("&eLong", true), ItemType.ARCHERY, SlotPos.of(3, 0)),
        OFFHAND_ITEMS_ITEM(ItemStackBuilder.from(Material.TOTEM_OF_UNDYING).name("&eOff Hand", true), ItemType.OFFHAND, SlotPos.of(4, 0)),
        NEXT_PAGE_ITEM(ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON).name("&a&lNext Page >>", true), ItemType.NULL),
        PREVIOUS_PAGE_ITEM(ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON).name("&c&l<< Previous Page", true), ItemType.NULL);

        ItemStack stack;
        SlotPos slotPos;
        ItemType type;

        GuiItem(ItemStackBuilder builder, ItemType type, SlotPos... slotPos) {
            this.stack = builder.flag(ItemFlag.HIDE_ATTRIBUTES).itemStack();
            Arrays.stream(slotPos).findFirst().ifPresent(s -> this.slotPos = s);
            this.type = type;
        }

        public ItemStack getStack() {
            return stack;
        }

        public SlotPos getSlotPos() {
            return slotPos;
        }

        public Icon getIcon() {
            return Icon.from(stack);
        }
    }

    private final HashMap<UUID, SlotPos> selected = new HashMap<>();

    @Override
    public void init(@NotNull InventoryContents contents) {
        contents.fill(Icon.cancel(GuiItem.BORDER_ITEM.getStack()));
        contents.fillSquare(SlotPos.of(1, 3), SlotPos.of(4, 7), Icon.EMPTY);

        final Pagination pagination = contents.pagination();
        final Player player = contents.player();

        AtomicReference<List<Item>> filteredItems = new AtomicReference<>(WyvenItems.instance.getItemManager().customItems);

        for (GuiItem category : GuiItem.values()) {
            if (category.slotPos == null) continue;

            contents.set(category.getSlotPos(), category.getIcon().whenClick(e -> {
                if (selected.containsKey(player.getUniqueId())) {
                    // REMOVE GLOWING EFFECT
                    init(contents);
                }

                selected.put(player.getUniqueId(), category.getSlotPos());

                filteredItems.set(WyvenItems.instance.getItemManager().customItems.stream().filter(i -> i.getType() == category.type).collect(Collectors.toList()));

                e.contents().set(category.getSlotPos(), Icon.click(ItemStackBuilder.from(category.getStack().clone()).glow().itemStack(), e1 -> {
                    // RESET FILTER IF CLICKED ON SELECTED
                    if (selected.get(player.getUniqueId()).equals(category.getSlotPos())) {
                        selected.remove(player.getUniqueId());
                        filteredItems.set(WyvenItems.instance.getItemManager().customItems);
                    }

                    init(contents);
                }));

                update(contents, filteredItems.get());
            }));
        }

        update(contents, filteredItems.get());

        if (!pagination.isFirst())
            contents.set(SlotPos.of(5, 4), Icon.click(GuiItem.PREVIOUS_PAGE_ITEM.getStack(), clickEvent -> contents.page().open(contents.player(), pagination.previous().getPage())));
        if (!pagination.isLast())
            contents.set(SlotPos.of(5, 6), Icon.click(GuiItem.NEXT_PAGE_ITEM.getStack(), clickEvent -> contents.page().open(contents.player(), pagination.next().getPage())));
    }

    public void update(@NotNull InventoryContents contents, List<Item> filter) {
        final Pagination pagination = contents.pagination();

//        Debug.log("items: " + filter.size());

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
    }
}
