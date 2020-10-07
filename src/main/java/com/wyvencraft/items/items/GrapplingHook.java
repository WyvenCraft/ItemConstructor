package com.wyvencraft.items.items;

import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.configuration.Message;
import com.wyvencraft.wyvencore.customitems.ItemManager;
import com.wyvencraft.wyvencore.customitems.items.StaticItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.FishHook;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class GrapplingHook implements Listener {
    public ItemStack grapplinghook;

    static Core plugin = Core.instance;

//    public static final NamespacedKey cooldownKey = new NamespacedKey(plugin, "cooldown");

    private final HashMap<UUID, Long> cooldown = new HashMap<>();

    public GrapplingHook() {
        grapplinghook = ItemManager.instance.loadStaticItem(StaticItem.GRAPPLINGHOOK);

        if (grapplinghook == null) {
            plugin.getLogger().severe("Grapplinghook feature has been disable: failed to load grapplinghook");
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onThrow(PlayerFishEvent e) {
        Player p = e.getPlayer();

        ItemStack itemInHand = p.getInventory().getItemInMainHand();
        if (!itemInHand.hasItemMeta()) return;

        if (itemInHand.isSimilar(grapplinghook)) {

            if (e.getState() == PlayerFishEvent.State.BITE) {
                e.setCancelled(true);
                return;
            }

            if (!plugin.getSettings().grapplingDisabledWorlds.contains(p.getWorld().getName())) {

                if (e.getState() == PlayerFishEvent.State.REEL_IN) {
                    if (cooldown.containsKey(e.getPlayer().getUniqueId())) {
                        long left = ((cooldown.get(e.getPlayer().getUniqueId()) / 1000) + plugin.getSettings().grapplingCooldown) - (System.currentTimeMillis() / 1000);
                        if (left > 0) {
                            p.sendMessage(Message.GRAPPLE_COOLDOWN.getChatMessage().replace("{cooldown}", plugin.DECIMALFORMAT.format(left)));
                            return;
                        }

                        cooldown.remove(e.getPlayer().getUniqueId());
                    }

                    grapple(e.getPlayer(), e.getHook());
                    cooldown.put(e.getPlayer().getUniqueId(), System.currentTimeMillis());
                }
            }
        }
    }

    public void grapple(Player p, FishHook hook) {
        if (!plugin.getSettings().grapplingUseInAir && !p.isOnGround()) return;
        p.playSound(p.getLocation(), plugin.getSettings().grapplingSound, 1, 1);

//        Vector vec = hook.getLocation().getDirection().add(p.getVelocity());
        p.setVelocity(p.getLocation().getDirection().multiply(3).setY(1));
//        itemInHand.getPDC().set(cooldown, PersistentDataType.LONG, System.currentTimeMillis());
    }
}
