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

import java.util.*;

public class OrbListener implements Listener {

    private final ItemManager itemManager;

    public OrbListener(ItemManager itemManager) {
        this.itemManager = itemManager;
    }

    public static final Map<Player, Map<Orb, Long>> orbCooldown = new HashMap<>();
    public static final List<UUID> activeOrb = new ArrayList<>();

    @EventHandler
    public void onPlaceOrb(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            if (itemManager.holdingItem(player, ItemType.ORB, false)) {
                event.setCancelled(true);
                if (activeOrb.contains(player.getUniqueId())) {
                    player.sendMessage("You can only have 1 orb active at a time!");
                    return;
                }

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

                Orb orb = (Orb) itemManager.getCustomItem(player.getInventory().getItemInMainHand(), ItemType.ORB);

                if (orbCooldown.containsKey(player)) {
                    if (orbCooldown.get(player).containsKey(orb)) {
                        double cooldown = orb.getCooldown() - orbCooldown.get(player).get(orb);
                        player.sendMessage("cooldown for another " + cooldown + " seconds");
                        return;
                    }
                }

                activeOrb.add(player.getUniqueId());
                Map<Orb, Long> cooldown = new HashMap<Orb, Long>() {{
                    put(orb, System.currentTimeMillis());
                }};
                orbCooldown.put(player, cooldown);

                new SpawnedOrb(orb, player, event.getClickedBlock().getLocation());
            }
        }
    }
}
