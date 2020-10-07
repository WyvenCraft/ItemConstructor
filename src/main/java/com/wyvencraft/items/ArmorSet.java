package com.wyvencraft.items;

import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.common.PDCItem;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class ArmorSet {

    private final String id;
    private final List<String> pieces;
    private final List<String> fullsetActions;

    public ArmorSet(String id, List<String> pieces, List<String> fullsetActions) {
        this.id = id;
        this.pieces = pieces;
        this.fullsetActions = fullsetActions;
    }

    public boolean isWearing(Player player) {
        if (getPieces().size() > player.getInventory().getArmorContents().length) return false;

        int wearingPieces = 0;
        for (String setPiece : getPieces()) {
            for (int i = 0; i < player.getInventory().getArmorContents().length; i++) {
                ItemStack piece = player.getInventory().getArmorContents()[i];
                if (piece == null || piece.getType() == Material.AIR) return false;
                PDCItem pdc1 = new PDCItem(piece);
                if (pdc1.getPDCString(Core.instance.WYVENKEY).equalsIgnoreCase(setPiece)) wearingPieces++;
            }
        }

        return wearingPieces == getPieces().size();
    }

    public String getId() {
        return id;
    }

    public List<String> getPieces() {
        return pieces;
    }

    public List<String> getFullsetActions() {
        return fullsetActions;
    }
}
