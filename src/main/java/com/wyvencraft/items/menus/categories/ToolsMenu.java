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

public class ToolsMenu extends ItemMenu {
    public ToolsMenu(List<Item> itemList) {
        super(itemList);
    }

    enum Type {
        PICKAXES(ItemStackBuilder.from(Material.IRON_PICKAXE).setName("&ePickaxes", true), ItemType.PICKAXE, SlotPos.of(0, 0)),
        AXES(ItemStackBuilder.from(Material.IRON_AXE).setName("&eAxes", true), ItemType.AXE, SlotPos.of(1, 0)),
        SHOVELS(ItemStackBuilder.from(Material.IRON_SHOVEL).setName("&eShovels", true), ItemType.SHOVEL, SlotPos.of(2, 0)),
        HOES(ItemStackBuilder.from(Material.IRON_HOE).setName("&eHoes", true), ItemType.HOE, SlotPos.of(3, 0)),
        FISHING_ROD(ItemStackBuilder.from(Material.FISHING_ROD).setName("&eFishing Rods", true), ItemType.FISHING_ROD, SlotPos.of(4, 0)),
        LIGHTER(ItemStackBuilder.from(Material.FLINT_AND_STEEL).setName("&eOther", true), ItemType.LIGHTER, SlotPos.of(5, 0));

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
