package com.wyvencraft.items.orbs;

import com.wyvencraft.items.WyvenItems;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.scheduler.BukkitRunnable;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class SpawnedOrb extends BukkitRunnable {
    private final OrbManager orbManager;

    private final Orb orb;
    private final Player owner;
    private final Location location;
    private final DecimalFormat df = new DecimalFormat("#0.0");
    private final Set<Player> applied = new HashSet<>();
    private final ArmorStand armorStand;

    int tick = 0;
    final double maxHeight = 1.5;
    boolean directionUp = true;

    double remaining;
    final float maxTicks;
    final float rotationSpeed;
    final double boppingSpeed;

    public SpawnedOrb(OrbManager orbManager, Orb orb, Player owner, Location location) {
        this.orbManager = orbManager;
        this.orb = orb;
        this.owner = owner;
        this.location = location;
        this.armorStand = location.getWorld().spawn(location, ArmorStand.class, armorStand -> {
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

        this.remaining = orb.getAliveTime();
        this.maxTicks = (float) (remaining * 20);
        this.rotationSpeed = (float) 360 / (maxTicks / 5);
        this.boppingSpeed = maxHeight / (maxTicks / 5);
    }

    @Override
    public synchronized void cancel() throws IllegalStateException {
        super.cancel();

        armorStand.remove();
        clearEffects();
        applied.clear();
        WyvenItems.instance.getOrbManager().activeOrb.remove(owner);
    }

    @Override
    public void run() {
        if (remaining <= 0 || !owner.isOnline()) {
            cancel();
            return;
        }

        // Bopping
        double direction = directionUp ? boppingSpeed : -boppingSpeed;
        Location updateLoc = armorStand.getLocation().add(0, direction, 0);
        // Rotate the orb
        updateLoc.setYaw(rotationSpeed * tick);
        // Update orb position
        armorStand.teleport(updateLoc);

        // If it has reached it's max bopping height, then reverse direction
        if (armorStand.getLocation().getY() >= (location.getY() + maxHeight)) directionUp = false;
        else if (armorStand.getLocation().getY() <= location.getY()) directionUp = true;

        armorStand.setCustomName("Despawning In " + df.format(remaining));

        if (tick % 5 == 0) applyEffects();
        if (tick % 2 == 0) {
            remaining -= 0.1;
        }

        tick++;
    }

    private void clearEffects() {
        for (Player p : applied) {
            if (p == null) continue;
            for (OrbModifier modifier : orb.getModifiers()) {
                modifier.clear(p);
            }
        }
    }

    private void applyEffects() {
        // Get all nearby players
        List<Player> nearbyPlayers = location.getNearbyEntities(orb.getRadius(), 2, orb.getRadius())
                .stream()
                .filter(ent -> ent instanceof Player)
                .map(ent -> (Player) ent)
                .collect(Collectors.toList());

        // Should we unsubscribe a player from the modifiers
        if (nearbyPlayers.size() != applied.size()) {
            for (Player player : applied) {
                // Do we have a player with modifiers, but who arent nearby anymore?
                if (!nearbyPlayers.contains(player)) {
                    for (OrbModifier modifier : orb.getModifiers()) {
                        modifier.clear(player);
                    }
                    applied.remove(player);
                }
            }
        }

        for (Player target : nearbyPlayers) {
            location.getWorld().spawnParticle(Particle.SPELL_INSTANT, target.getLocation(), 5, 0, 0.2, 0, 5, null, false);
            if (applied.contains(target)) continue;

            // TODO Use WyvenGuilds to check if the target player is friendly or not.
            orbManager.applyModifiers(target, orb, false);
            applied.add(target);
        }
    }
}
