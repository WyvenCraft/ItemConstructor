package com.wyvencraft.items.data;

import com.wyvencraft.items.WyvenItems;
import io.github.portlek.bukkititembuilder.ItemStackBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class ArmorSet {

    private final String id;
    private final List<String> pieces;
    private final List<String> fullSetActions;

    public ArmorSet(String id, List<String> pieces, List<String> fullSetActions) {
        this.id = id;
        this.pieces = pieces;
        this.fullSetActions = fullSetActions;
    }

    public boolean hasFullSet(Player player) {
        if (getPieces().size() > player.getInventory().getArmorContents().length) return false;

        int wearingPieces = 0;
        for (ItemStack armorPiece : player.getInventory().getArmorContents()) {

            PersistentDataContainer pdc = ItemStackBuilder.from(armorPiece).getItemMeta().getPersistentDataContainer();
            if (!pdc.has(WyvenItems.WYVEN_ITEM, PersistentDataType.STRING)) continue;
            String piece = pdc.get(WyvenItems.WYVEN_ITEM, PersistentDataType.STRING);

            for (String setPiece : getPieces()) if (setPiece.equals(piece)) wearingPieces++;
        }

        return wearingPieces == getPieces().size();
    }

    public String getId() {
        return id;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public List<String> getFullSetActions() {
        return fullSetActions;
    }
}
