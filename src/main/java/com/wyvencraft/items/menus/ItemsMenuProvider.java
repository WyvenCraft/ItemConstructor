package com.wyvencraft.items.menus;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import io.github.portlek.smartinventory.*;
import io.github.portlek.smartinventory.util.SlotPos;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemsMenuProvider implements InventoryProvider {

    private final ItemStack borderItem = ItemStackBuilder.from(Material.GRAY_STAINED_GLASS_PANE)
            .name(" ")
            .itemStack();
    private final ItemStack helmetsItem = ItemStackBuilder.from(Material.DIAMOND_HELMET)
            .name("&eHelmets", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack chestplatesItem = ItemStackBuilder.from(Material.DIAMOND_CHESTPLATE)
            .name("&eChestplates", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack leggingsItem = ItemStackBuilder.from(Material.DIAMOND_LEGGINGS)
            .name("&eLeggings", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack bootsItem = ItemStackBuilder.from(Material.DIAMOND_BOOTS)
            .name("&eBoots", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack combatItem = ItemStackBuilder.from(Material.DIAMOND_SWORD)
            .name("&eCombat", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack toolsItem = ItemStackBuilder.from(Material.DIAMOND_PICKAXE)
            .name("&eTools", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack archeryItem = ItemStackBuilder.from(Material.BOW)
            .name("&eLong Range", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack offHandItem = ItemStackBuilder.from(Material.TOTEM_OF_UNDYING)
            .name("&eOff Hand", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack nextPage = ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON)
            .name("&a&lNext Page >>", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();
    private final ItemStack previousPage = ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON)
            .name("&c&l<< Previous Page", true)
            .flag(ItemFlag.HIDE_ATTRIBUTES)
            .itemStack();

    private final HashMap<UUID, SlotPos> category = new HashMap<>();

    @Override
    public void init(@NotNull InventoryContents contents) {
        contents.fill(Icon.cancel(borderItem));
        contents.fillSquare(SlotPos.of(1, 3), SlotPos.of(4, 7), Icon.EMPTY);

        final Pagination pagination = contents.pagination();

        List<Item> filteredItems = WyvenItems.instance.getItemManager().customItems;

        contents.set(SlotPos.of(1, 0), Icon.click(combatItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(combatItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(2, 0), Icon.click(toolsItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(toolsItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(3, 0), Icon.click(archeryItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(archeryItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(4, 0), Icon.click(offHandItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(offHandItem).glow().itemStack());
        }));

        contents.set(SlotPos.of(1, 1), Icon.click(helmetsItem, clickEvent -> {
//            filteredItems.set(filteredItems.get().stream()
//                    .filter(i -> i.getName().toUpperCase().endsWith("_HELMET"))
//                    .collect(Collectors.toList()));

            clickEvent.icon().item(ItemStackBuilder.from(helmetsItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(2, 1), Icon.click(chestplatesItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(chestplatesItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(3, 1), Icon.click(leggingsItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(leggingsItem).glow().itemStack());
        }));
        contents.set(SlotPos.of(4, 1), Icon.click(bootsItem, clickEvent -> {
            clickEvent.icon().item(ItemStackBuilder.from(bootsItem).glow().itemStack());
        }));

        final Icon[] icons = new Icon[filteredItems.size()];
        for (int i = 0; i < icons.length; i++) {
            final Item cItem = filteredItems.get(i);
            icons[i] = Icon.click(cItem.getItem(), clickEvent -> {
                WyvenItems.instance.getItemManager().giveItem(contents.player(), cItem, 1);
            });
        }

        pagination.setIcons(icons);
        pagination.setIconsPerPage(20);

        pagination.addToIterator(contents.newIterator(SlotIterator.Type.HORIZONTAL, 1, 3)
                .allowOverride(true)
                .blacklist(1, 3).blacklist(1, 4).blacklist(1, 5).blacklist(1, 6).blacklist(1, 7)
                .blacklist(2, 3).blacklist(2, 4).blacklist(2, 5).blacklist(2, 6).blacklist(2, 7)
                .blacklist(3, 3).blacklist(3, 4).blacklist(3, 5).blacklist(3, 6).blacklist(3, 7)
                .blacklist(4, 3).blacklist(4, 4).blacklist(4, 5).blacklist(4, 6).blacklist(4, 7));

        if (!pagination.isFirst())
            contents.set(SlotPos.of(5, 4), Icon.click(previousPage, clickEvent -> contents.page().open(contents.player(), pagination.previous().getPage())));
        if (!pagination.isLast())
            contents.set(SlotPos.of(5, 6), Icon.click(nextPage, clickEvent -> contents.page().open(contents.player(), pagination.next().getPage())));
    }
}
