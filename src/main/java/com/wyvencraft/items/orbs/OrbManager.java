package com.wyvencraft.items.orbs;

import com.wyvencraft.items.WyvenItems;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class OrbManager {
    private final WyvenItems addon;

    public OrbManager(WyvenItems addon) {
        this.addon = addon;
    }

    private final Map<UUID, LinkedHashMap<Orb, Long>> orbCooldown = new HashMap<>();
    protected final Map<Player, SpawnedOrb> activeOrb = new HashMap<>();

    public void disableOrb(Player player) {
        final SpawnedOrb active = activeOrb.getOrDefault(player, null);
        if (active == null) return;
        active.cancel();
        activeOrb.remove(player);
    }

    public void clearEffects(Player player, Orb orb) {
        for (OrbModifier modifier : orb.getModifiers()) {
            modifier.clear(player);
        }
    }

    public void applyModifiers(Player player, Orb orb, boolean onInit) {
        for (OrbModifier modifier : orb.getModifiers()) {
            // Check if modifier should be given on init
            if (modifier.isOnlyOnInit() && !onInit) continue;

            modifier.apply(player);
        }
    }

    public void activateOrb(Player player, Orb orb, Location location) {
        SpawnedOrb spawnedOrb = new SpawnedOrb(this, orb, player, location);
        spawnedOrb.runTaskTimer(addon.getPlugin().getPlugin(), 0, 1);
        activeOrb.put(player, spawnedOrb);
        startCooldown(player, orb);
    }

    public void startCooldown(Player player, Orb orb) {
        // By doing it this way, the player has a seperate cooldown time for each orb
        LinkedHashMap<Orb, Long> cooldown = orbCooldown.getOrDefault(player.getUniqueId(), new LinkedHashMap<>());
        cooldown.put(orb, System.currentTimeMillis());
        orbCooldown.put(player.getUniqueId(), cooldown);
    }

    public boolean isOrbInCooldown(Player player, Orb orb) {
        if (!orbCooldown.containsKey(player.getUniqueId())) return false;
        return orbCooldown.get(player.getUniqueId()).containsKey(orb);
    }

    public double getCooldown(Player player, Orb orb) {
        if (!isOrbInCooldown(player, orb)) return 0.0d;

        // Get cooldown left in seconds
        return orb.getCooldown() - orbCooldown.get(player.getUniqueId()).get(orb);
    }

    public boolean hasActiveOrb(Player player) {
        return activeOrb.containsKey(player);
    }

    public void clearAllOrbs() {
        for (Map.Entry<Player, SpawnedOrb> entry : activeOrb.entrySet()) {
            disableOrb(entry.getKey());
        }
    }
}
