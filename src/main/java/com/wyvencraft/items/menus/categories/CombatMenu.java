package com.wyvencraft.items.menus.categories;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.data.Item;
import com.wyvencraft.items.enums.ItemType;
import com.wyvencraft.smartinventory.Icon;
import com.wyvencraft.smartinventory.InventoryContents;
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
import java.util.stream.Collectors;

public class CombatMenu extends ItemMenu {
    public CombatMenu(List<Item> itemList) {
        super(itemList);
    }

    enum Type {
        SWORDS(ItemStackBuilder.from(Material.IRON_SWORD).setName("&eSwords", true), ItemType.SWORD, SlotPos.of(1, 0)),
        LONG_RANGE(ItemStackBuilder.from(Material.BOW).setName("&eRange Combat", true), ItemType.BOW, SlotPos.of(2, 0)),
        AXES(ItemStackBuilder.from(Material.NETHERITE_AXE).setName("&eBattle Axes", true), ItemType.BATTLE_AXE, SlotPos.of(3, 0)),
        SHIELDS(ItemStackBuilder.from(Material.SHIELD).setName("&eShields", true), ItemType.SHIELD, SlotPos.of(4, 0));

        final ItemStack itemStack;
        final ItemType type;
        final SlotPos slotPos;

        Type(ItemStackBuilder builder, ItemType type, SlotPos slotPos) {
            this.itemStack = builder.addFlag(ItemFlag.HIDE_ATTRIBUTES).getItemStack();
            this.type = type;
            this.slotPos = slotPos;
        }
    }

    private final HashMap<UUID, Type> selected = new HashMap<>();

    @Override
    public void init(@NotNull InventoryContents contents) {
        super.init(contents);

        final Player player = contents.player();

        contents.page().whenClose(closeEvent -> selected.remove(player.getUniqueId()));

        for (Type type : Type.values()) {
            contents.set(type.slotPos, Icon.click(type.itemStack, e -> {
                if (selected.containsKey(player.getUniqueId())) {
                    Type select = selected.get(player.getUniqueId());
                    e.contents().set(select.slotPos, contents.get(select.slotPos).get().item(select.itemStack));
                }

                selected.put(player.getUniqueId(), type);
                List<Item> filtered = setSelected(contents, player, type);

                update(contents, filtered);
            }));
        }
        update(contents, getItemList());
    }

    private List<Item> setSelected(InventoryContents contents, Player player, Type type) {
        final ItemStack glowingCategory = ItemStackBuilder.from(type.itemStack.clone())
                .addGlowEffect()
                .getItemStack();

        contents.set(type.slotPos, Icon.click(glowingCategory, e1 -> {
            selected.remove(player.getUniqueId());
            init(contents);
        }));

        // Filter items to the correct category
        return WyvenItems.instance.getItemManager().customItems.stream()
                .filter(i -> i.getType() == type.type)
                .collect(Collectors.toList());
    }
}
