package com.wyvencraft.items.data;

import com.wyvencraft.items.WyvenItems;
import com.wyvencraft.items.listeners.OrbListener;
import com.wyvencraft.items.utils.Debug;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

public class SpawnedOrb implements Listener {
//    public static List<ArmorStand> activeOrbs = new ArrayList<>();

    public final Orb orb;
    public final Player owner;
    public final Location location;

    private ArmorStand armorStand;

    private final DecimalFormat df = new DecimalFormat("#0.0");

    private final Set<Player> applied = new HashSet<>();

    public SpawnedOrb(Orb orb, Player owner, Location location) {
        this.orb = orb;
        this.owner = owner;
        this.location = location.toCenterLocation().subtract(0, 1, 0);

        spawnOrb();
    }

    private void spawnOrb() {
        Plugin plugin = WyvenItems.instance.getPlugin().getPlugin();
        armorStand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCanTick(false);
            armorStand.setInvulnerable(true);
            armorStand.setRemoveWhenFarAway(true);
            armorStand.setCollidable(false);
            armorStand.setItem(EquipmentSlot.HEAD, orb.getSkull());
            armorStand.addDisabledSlots(EquipmentSlot.values());
        });

//        activeOrbs.add(armorStand);
        applyEffects(true);

        new BukkitRunnable() {
            double timeToDespawn = orb.getAliveTime();
            int tick = 0;
            final float maxTicks = (float) (timeToDespawn * 20);
            final float rotationSpeed = (float) 360 / (maxTicks / 5);
            final double maxHeight = 1.5;
            final double boppingSpeed = maxHeight / (maxTicks / 5);
            boolean directionUp = true;

            @Override
            public void run() {
                if (timeToDespawn <= 0 || !owner.isOnline() || armorStand.isDead()) {
                    removeEffects();

                    armorStand.remove();
                    cancel();
                    OrbListener.orbCooldown.get(owner.getUniqueId()).remove(orb);
                    OrbListener.activeOrb.remove(owner.getUniqueId());
                    return;
                }

                double direction = directionUp ? boppingSpeed : -boppingSpeed;
                Location updateLoc = armorStand.getLocation().add(0, direction, 0);
                updateLoc.setYaw(rotationSpeed * tick);

                armorStand.teleport(updateLoc);

                if (armorStand.getLocation().getY() >= (location.getY() + maxHeight)) directionUp = false;
                else if (armorStand.getLocation().getY() <= location.getY()) directionUp = true;

                armorStand.setCustomName("Despawning In " + df.format(timeToDespawn));

                if (tick % 5 == 0) applyEffects(false);

                tick++;
                if (tick % 2 == 0) {
                    timeToDespawn -= 0.1;
                }
            }
        }.runTaskTimer(plugin, 1, 1);
    }

    private void applyEffects(boolean initModifiers) {
        List<Player> nearbyPlayers = location.getNearbyEntities(orb.getRadius(), 2, orb.getRadius())
                .stream()
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent)
                .collect(Collectors.toList());

        if (nearbyPlayers.size() != applied.size()) {
            for (Player player : applied) {
                if (!nearbyPlayers.contains(player)) removeEffects(player, true);
            }
        }

        for (Player target : nearbyPlayers) {
            location.getWorld().spawnParticle(Particle.SPELL_INSTANT, target.getLocation(), 5, 0, 0.2, 0, 5, null, false);

            if (applied.contains(target)) continue;

            if (!target.hasMetadata(owner.getUniqueId().toString())) {
                target.setMetadata(owner.getUniqueId().toString(), new FixedMetadataValue(WyvenItems.instance.getPlugin().getPlugin(), 0));
            }

            for (OrbModifier modifier : orb.getModifiers()) {
                if (modifier.isOnlyOnInit() && !initModifiers) continue;
                // TODO Use WyvenGuilds to check if the target player is friendly or not.

                modifier.apply(target);
            }

            applied.add(target);
        }
    }

    private void removeEffects() {
        for (Player player : applied) {
            removeEffects(player, false);
        }

        applied.clear();
    }

    public void removeEffects(Player player, boolean remove) {
        for (OrbModifier modifier : orb.getModifiers()) {
            modifier.clear(player);
        }

        player.removeMetadata(owner.getUniqueId().toString(), WyvenItems.instance.getPlugin().getPlugin());
        if (remove) applied.remove(player);
    }
}
