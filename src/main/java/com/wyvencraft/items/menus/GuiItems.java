package com.wyvencraft.items.menus;

import com.wyvencraft.smartinventory.util.SlotPos;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

public enum GuiItems {
    BORDER_ITEM(ItemStackBuilder.from(Material.GRAY_STAINED_GLASS_PANE).setName(" "), null),
    NEXT_PAGE_ITEM(ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON).setName("&a&lNext Page >>", true), SlotPos.of(5, 7)),
    PREVIOUS_PAGE_ITEM(ItemStackBuilder.from(Material.POLISHED_BLACKSTONE_BUTTON).setName("&c&l<< Previous Page", true), SlotPos.of(5, 6)),
    BACK_ITEM(ItemStackBuilder.from(Material.BOOK).setName("&c&lBack to All", true), SlotPos.of(0, 2));

    public final ItemStack itemStack;
    public final SlotPos slotPos;

    GuiItems(ItemStackBuilder builder, SlotPos slotPos) {
        this.itemStack = builder.addFlag(ItemFlag.HIDE_ATTRIBUTES).getItemStack();
        this.slotPos = slotPos;
    }
}
