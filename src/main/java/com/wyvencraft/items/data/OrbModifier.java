package com.wyvencraft.items.data;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;

public class OrbModifier {

    public enum OrbTarget {
        FRIENDLY, NEUTRAL, ENEMY
    }

    private final PotionEffect potionEffect;
    private final AttributeModifier attributes;
    private final OrbTarget target;
    private final boolean onlyOnInit;

    public OrbModifier(PotionEffect potionEffect, AttributeModifier attributes, OrbTarget target, boolean onlyOnInit) {
        this.potionEffect = potionEffect;
        this.attributes = attributes;
        this.target = target;
        this.onlyOnInit = onlyOnInit;
    }

    public PotionEffect getPotionEffect() {
        return potionEffect;
    }

    public AttributeModifier getAttributes() {
        return attributes;
    }

    public OrbTarget getTarget() {
        return target;
    }

    public boolean isOnlyOnInit() {
        return onlyOnInit;
    }

    public void apply(Player player) {
        if (getPotionEffect() != null)
            player.addPotionEffect(getPotionEffect());
        if (getAttributes() != null) {
            Attribute attribute = Attribute.valueOf("GENERIC_" + getAttributes().getName().toUpperCase());
            player.getAttribute(attribute).addModifier(getAttributes());
        }
    }

    public void clear(Player player) {
        if (getPotionEffect() != null)
            player.removePotionEffect(getPotionEffect().getType());
        if (getAttributes() != null) {
            Attribute attribute = Attribute.valueOf("GENERIC_" + getAttributes().getName().toUpperCase());
            player.getAttribute(attribute).removeModifier(getAttributes());
        }
    }
}
