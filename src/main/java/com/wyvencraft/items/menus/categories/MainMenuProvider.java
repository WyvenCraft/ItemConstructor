package com.wyvencraft.items.menus.categories;

import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.enums.Category;
import com.wyvencraft.items.menus.ItemsMenu;
import com.wyvencraft.smartinventory.Icon;
import com.wyvencraft.smartinventory.InventoryContents;
import com.wyvencraft.smartinventory.util.SlotPos;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MainMenuProvider extends ItemMenu {
    private final ItemsMenu menus;

    public MainMenuProvider(List<Item> itemList, ItemsMenu menus) {
        super(itemList);
        this.menus = menus;
    }

    enum CategoryItem {
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
    }

    @Override
    public void init(@NotNull InventoryContents contents) {
        super.init(contents);

        for (CategoryItem category : CategoryItem.values()) {
            contents.set(category.slotPos, Icon.click(category.itemStack, e -> {
                menus.open(contents.player(), category.type);
            }));
        }

        update(contents, getItemList());
    }
}
