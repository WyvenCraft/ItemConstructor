package com.wyvencraft.items.items;

import com.wyvencraft.wyvencore.Core;
import com.wyvencraft.wyvencore.customitems.ItemManager;
import com.wyvencraft.wyvencore.customitems.items.StaticItem;
import com.wyvencraft.wyvencore.hooks.HeadDatabaseHook;
import com.wyvencraft.wyvencore.utils.Debug;
import com.wyvencraft.wyvencore.utils.Methods;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class Dummy implements Listener {
    public ItemStack dummyItem;

    static Core plugin = Core.instance;

    public Dummy() {
        dummyItem = ItemManager.instance.loadStaticItem(StaticItem.DUMMY);

        if (dummyItem == null) {
            plugin.getLogger().severe("Dummy item has been disable: failed to load dummy");
            return;
        }

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlaceDummy(PlayerInteractEvent e) {
        ItemStack stack = e.getPlayer().getInventory().getItemInMainHand();
        if (stack.isSimilar(dummyItem)) {
            e.setCancelled(true);

            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                Location loc = e.getClickedBlock().getRelative(e.getBlockFace()).getLocation().add(0.5, 0, 0.5);
                loc.setDirection(loc.getDirection().subtract(e.getPlayer().getVelocity()));

                loc.getWorld().spawn(loc, ArmorStand.class, dummy -> {
                    dummy.setCollidable(false);
                    dummy.setMetadata("dummy", new FixedMetadataValue(plugin, 2));
                    dummy.setArms(true);
                    dummy.setBasePlate(false);
                    dummy.setItem(EquipmentSlot.HEAD, HeadDatabaseHook.getHdbInstance().getHead("19395"));
                    dummy.setItem(EquipmentSlot.CHEST, new ItemStack(Material.LEATHER_CHESTPLATE));
                });

                e.getPlayer().getInventory().getItemInMainHand().setAmount(stack.getAmount() - 1);
            }

            Debug.log("placed dummy");
        }
    }

    @EventHandler
    public void onPickUpDummy(PlayerInteractAtEntityEvent e) {
        if (e.getRightClicked() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) e.getRightClicked();
            if (stand.hasMetadata("dummy")) {
                if (e.getPlayer().isSneaking()) {
                    Debug.log("pick up dummy");

                    stand.remove();
                    Methods.addItemsToPlayer(e.getPlayer(), stand.getEyeLocation(), new ItemStack[]{dummyItem});
                }
            }
        }
    }

//    @EventHandler
//    public void onDamageDummy(EntityDamageByEntityEvent e) {
//        if (e.getEntityType() == EntityType.ARMOR_STAND) {
//            ArmorStand stand = (ArmorStand) e.getEntity();
//            if (stand.hasMetadata("dummy")) {
//            }
//        }
//    }

    @EventHandler
    public void onDamageDead(EntityDeathEvent e) {
        if (e.getEntity() instanceof ArmorStand) {
            ArmorStand stand = (ArmorStand) e.getEntity();
            if (stand.hasMetadata("dummy")) {
                Debug.log("dummy died");

                e.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDummyInteract(PlayerArmorStandManipulateEvent e) {
        if (e.getRightClicked().hasMetadata("dummy")) e.setCancelled(true);
    }
}
