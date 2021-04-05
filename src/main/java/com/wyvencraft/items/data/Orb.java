package com.wyvencraft.items.data;

import org.bukkit.inventory.ItemStack;

import java.util.List;

public class Orb extends Item {

    private ItemStack skull;
    private List<OrbModifier> modifiers;
    private double radius;
    private double aliveTime;
    private boolean bopping;

    public Orb(Item item, ItemStack skull, List<OrbModifier> modifiers, double radius, double aliveTime, boolean bopping) {
        super(item);
        this.skull = skull;
        this.modifiers = modifiers;
        this.radius = radius;
        this.aliveTime = aliveTime;
        this.bopping = bopping;
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

    public boolean isBopping() {
        return bopping;
    }
}
