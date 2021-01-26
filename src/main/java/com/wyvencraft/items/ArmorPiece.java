package com.wyvencraft.items;


import org.bukkit.attribute.Attribute;

import java.util.Map;

public class ArmorPiece {

    private final Item item;
    private final Map<Attribute, Double> bonusAttributes;

    public ArmorPiece(Item item, Map<Attribute, Double> bonusAttributes) {
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
