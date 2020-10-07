package com.wyvencraft.items;

import com.wyvencraft.wyvencore.customitems.Item;

public class ArmorPiece {

    private final com.wyvencraft.wyvencore.customitems.Item item;
    private final Map<Attribute, Double> bonusAttributes;

    public ArmorPiece(com.wyvencraft.wyvencore.customitems.Item item, Map<Attribute, Double> bonusAttributes) {
        this.item = item;
        this.bonusAttributes = bonusAttributes;
    }

    public Map<Attribute, Double> getBonusAttributes() {
        return bonusAttributes;
    }

    public Item getItem() {
        return item;
    }
}
