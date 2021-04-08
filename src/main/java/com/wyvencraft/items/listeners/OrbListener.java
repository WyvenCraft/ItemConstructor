package com.wyvencraft.items.listeners;

import com.wyvencraft.items.data.Orb;
import com.wyvencraft.items.data.SpawnedOrb;
import com.wyvencraft.items.enums.ItemType;
import com.wyvencraft.items.managers.ItemManager;
import com.wyvencraft.items.utils.Debug;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;

public class OrbListener implements Listener {

    private final ItemManager itemManager;

    public OrbListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public static final Map<UUID, LinkedHashMap<Orb, Long>> orbCooldown = new HashMap<>();
    public static final Map<UUID, SpawnedOrb> activeOrb = new HashMap<>();

    @EventHandler
    public void onPlaceOrb(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (itemManager.holdingItem(player, ItemType.ORB, false)) {
                event.setCancelled(true);

                // Check to see if the player is already having an active orb
                if (activeOrb.containsKey(player.getUniqueId())) {
                    player.sendMessage("You can only have 1 orb active at a time!");
                    return;
                }

                // Check to see if blocks are blocking the orb
                Block block = event.getClickedBlock();
                boolean isBlocked = false;
                for (int i = 1; i <= 2; i++) {
                    if (block.getRelative(BlockFace.UP, i).getType() != Material.AIR) {
                        isBlocked = true;
                        Debug.log("bruh");
                        break;
                    }
                }

                if (isBlocked) {
                    player.sendMessage("Not enough room above orb to spawn here");
                    return;
                }

                // Get the orb from player's hand
                Orb orb = (Orb) itemManager.getCustomItem(player.getInventory().getItemInMainHand(), ItemType.ORB);

                // Check if orb is on cooldown
                if (orbCooldown.containsKey(player.getUniqueId())) {
                    if (orbCooldown.get(player.getUniqueId()).containsKey(orb)) {
                        double cooldown = orb.getCooldown() - orbCooldown.get(player.getUniqueId()).get(orb);
                        player.sendMessage("cooldown for another " + cooldown + " seconds");
                        return;
                    }
                }

                // Start cooldown
                LinkedHashMap<Orb, Long> cooldown = orbCooldown.getOrDefault(player.getUniqueId(), new LinkedHashMap<>());
                cooldown.put(orb, System.currentTimeMillis());
                orbCooldown.put(player.getUniqueId(), cooldown);

                // Activate orb
                activeOrb.put(
                        player.getUniqueId(),
                        new SpawnedOrb(orb, player, event.getClickedBlock().getLocation())
                );
            }
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        for (Map.Entry<UUID, SpawnedOrb> entry : activeOrb.entrySet()) {
            if (player.hasMetadata(entry.getKey().toString())) {
                entry.getValue().removeEffects(player, true);
            }
        }
    }
}
