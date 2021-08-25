package com.wyvencraft.items.orbs;

import com.wyvencraft.items.data.Item;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Orb extends Item {

    private final ItemStack skull;
    private final List<OrbModifier> modifiers;
    private final double radius;
    private final double aliveTime;
    private final double cooldown;

    public Orb(Item item, ItemStack skull, List<OrbModifier> modifiers, double radius, double aliveTime, double cooldown) {
        super(item);
        this.skull = skull;
        this.modifiers = modifiers;
        this.radius = radius;
        this.aliveTime = aliveTime;
        this.cooldown = cooldown;
    }

    public ItemStack getSkull() {
        return skull;
    }

    public List<OrbModifier> getModifiers() {
        return modifiers;
    }

    public double getRadius() {
        return radius;
    }

    public double getAliveTime() {
        return aliveTime;
    }

    public double getCooldown() {
        return cooldown;
    }
}
