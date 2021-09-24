package com.wyvencraft.items.orbs;

import com.wyvencraft.items.enums.ItemType;
import com.wyvencraft.items.managers.ItemManager;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

public record OrbListener(ItemManager itemManager,
                          OrbManager orbManager) implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlaceOrb(BlockPlaceEvent event) {
        final Player player = event.getPlayer();

        final ItemStack hand = itemManager.getHand(player, true);
        if (hand == null) return;

        if (itemManager.isItemOfType(ItemType.ORB, hand)) {
            event.setCancelled(true);

            // Check to see if the player is already having an active orb
            if (orbManager.hasActiveOrb(player)) {
                player.sendMessage("You can only have 1 orb active at a time!"); //TODO ORB_ALREADY_ACTIVE
                return;
            }

            // Check to see if blocks are blocking the orb
            Block block = event.getBlock();
            for (int i = 1; i <= 2; i++) {
                if (block.getRelative(BlockFace.UP, i).getType() != Material.AIR) {
                    player.sendMessage("Not enough room above orb to spawn here");//TODO ROOM_UNAVAILABLE
                    return;
                }
            }

            // Get the orb from player's hand
            Orb orb = (Orb) itemManager.getItem(hand);

            // Check if orb is on cooldown
            double cooldown = orbManager.getCooldown(player, orb);
            player.sendMessage("cooldown: " + cooldown);
            if (cooldown > 0.0d) {
                player.sendMessage("cooldown for another " + cooldown + " seconds");//TODO ORB_COOLDOWN
                return;
            }

            // Activate orb
            orbManager.activateOrb(player, orb, block.getLocation());
        }
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        final Player player = event.getPlayer();
        if (orbManager.hasActiveOrb(player)) {
            orbManager.disableOrb(player);
        }
    }
}
