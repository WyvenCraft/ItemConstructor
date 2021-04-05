package com.wyvencraft.items.data;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.potion.PotionEffectType;

public class OrbModifier {

    public enum OrbTarget {
        FRIENDLY, NEUTRAL, ENEMY
    }

    private final PotionEffectType potionEffect;
    private final AttributeModifier attributes;
    private final OrbTarget target;

    public OrbModifier(PotionEffectType potionEffect, AttributeModifier attributes, OrbTarget target) {
        this.potionEffect = potionEffect;
        this.attributes = attributes;
        this.target = target;
    }

    public PotionEffectType getPotionEffect() {
        return potionEffect;
    }

    public AttributeModifier getAttributes() {
        return attributes;
    }

    public OrbTarget getTarget() {
        return target;
    }
}
