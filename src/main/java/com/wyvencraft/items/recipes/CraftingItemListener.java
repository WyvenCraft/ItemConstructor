package com.wyvencraft.items.recipes;

import org.bukkit.event.Listener;

public class CraftingItemListener implements Listener {
//    Core plugin = Core.instance;

//    @EventHandler
//    public void onCraftingTableClick(InventoryClickEvent e) {
//        if (e.getClickedInventory() instanceof CraftingInventory) {
//            Bukkit.getPluginManager().callEvent(
//                    new PrepareItemCraftEvent((CraftingInventory) e.getClickedInventory(),
//                            e.getView(), false));
//        }
//    }
//
//    @EventHandler
//    public void onPrepareCraft(PrepareItemCraftEvent e) {
//        if (e.getRecipe() != null) {
//
//            ItemStack result = e.getRecipe().getResult();
//
//            if (!result.hasItemMeta()) return;
//
//            Recipe recipe = Recipe.customRecipes.get(result);
//
//            Debug.log(recipe.name);
//
//            if (recipe != null) {
//                Debug.log("test1");
//                for (ItemStack ingred : recipe.ingredients.values()) {
//                    if (ingred.getType() == Material.AIR) continue;
//
//                    for (ItemStack item : e.getInventory().getMatrix()) {
//                        if (item == null) continue;
//
//                        if (item.isSimilar(ingred)) {
//
//                            Debug.log("test 2: " + item.getAmount());
//
//                            if (item.getAmount() < ingred.getAmount()) {
//                                Debug.log("test 3");
//                                e.getInventory().setResult(new ItemStack(Material.AIR));
//                                return;
//                            }
//                        }
//                    }
//                }
//
//                for (HumanEntity he : e.getViewers()) {
//                    if (!he.hasDiscoveredRecipe(recipe.customItem.getKey())) {
//                        e.getInventory().setResult(new ItemStack(Material.AIR));
//                    }
//                }
//            }
//        }
//    }
//
//    @EventHandler
//    public void onItemCraft(CraftItemEvent e) {
//        if (e.getInventory().getResult() == null) return;
//
//
//        ItemStack result = e.getRecipe().getResult();
//
//        if (!result.hasItemMeta()) return;
//
//        Recipe recipe = Recipe.customRecipes.get(result);
//        if (recipe != null) {
//            for (ItemStack ingred : recipe.ingredients.values()) {
//                if (ingred.getType() == Material.AIR) continue;
//
//                for (ItemStack slot : e.getInventory().getMatrix()) {
//                    if (slot == null) continue;
//
//                    if (slot.isSimilar(ingred)) slot.setAmount(slot.getAmount() - ingred.getAmount());
//                }
//            }
//        }
//    }
}
