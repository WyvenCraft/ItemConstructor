package com.wyvencraft.items.utils;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Cooldown extends BukkitRunnable {

    private final Player player;
    private final double cooldown;
    private double time;

    public Cooldown(Player player, double cooldown) {
        this.player = player;
        this.cooldown = cooldown;

    }

    @Override
    public void run() {

    }
}
