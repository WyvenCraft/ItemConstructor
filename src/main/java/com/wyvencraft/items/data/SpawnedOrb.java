package com.wyvencraft.items.data;

import com.wyvencraft.items.WyvenItems;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnedOrb {
    public static List<ArmorStand> activeOrbs;

    public final Orb orb;
    public final Player owner;
    public Location location;

    private ArmorStand armorStand;

    public SpawnedOrb(Orb orb, Player owner, Location location) {
        this.orb = orb;
        this.owner = owner;
        this.location = location;
    }

    private void spawnOrb() {
        Plugin plugin = WyvenItems.instance.getPlugin().getPlugin();
        location.getWorld().spawn(location.add(0, 2.5, 0), ArmorStand.class, armorStand -> {
            armorStand.setCustomNameVisible(true);
            armorStand.setVisible(false);
            armorStand.setGravity(false);
            armorStand.setCanTick(false);
            armorStand.setInvulnerable(true);
//            indicator.setMetadata("Orb", new FixedMetadataValue(plugin, true));
            armorStand.setRemoveWhenFarAway(true);
            armorStand.setCollidable(false);
            armorStand.setItem(EquipmentSlot.HEAD, orb.getSkull());
            armorStand.addDisabledSlots(EquipmentSlot.values());

            activeOrbs.add(armorStand);

            new BukkitRunnable() {
                double timeToDespawn = orb.getAliveTime();
                int tick = 0;

                @Override
                public void run() {
                    if (timeToDespawn <= 0 || owner == null || !owner.isOnline()) {
                        // TODO Remove buffs and debuffs from player
                        armorStand.remove();
                        cancel();
                        return;
                    }

                    armorStand.setCustomName("Despawning In " + timeToDespawn);


                    tick++;
                    if (tick % 5 == 0) {
                        timeToDespawn -= 0.25;
                        tick = 0;
                    }
                }
            }.runTaskTimer(plugin, 1, 1);
        });
    }
}
