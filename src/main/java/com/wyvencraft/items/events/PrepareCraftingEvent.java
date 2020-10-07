package com.wyvencraft.items.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.Recipe;
import org.jetbrains.annotations.NotNull;

public class PrepareCraftingEvent extends Event implements Cancellable {

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean cancelled;

    private final Recipe recipe;
    private final boolean customRecipe;
    private final Player player;
    private final boolean repair;

    public PrepareCraftingEvent(Recipe recipe, boolean customRecipe, Player player, boolean repair) {
        this.recipe = recipe;
        this.customRecipe = customRecipe;
        this.player = player;
        this.repair = repair;

        this.cancelled = false;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public boolean isCustomRecipe() {
        return customRecipe;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isRepair() {
        return repair;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public @NotNull HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }
}
