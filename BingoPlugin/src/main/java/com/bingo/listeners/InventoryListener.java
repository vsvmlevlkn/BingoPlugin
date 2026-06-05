package com.bingo.listeners;

import com.bingo.BingoPlugin;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

/**
 * Listens to all inventory-related events that can change what items a player holds.
 * On each event, delegates to GameManager for progress tracking.
 */
public class InventoryListener implements Listener {

    private final BingoPlugin plugin;

    public InventoryListener(BingoPlugin plugin) {
        this.plugin = plugin;
    }

    // ─── Pick up items ────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPickupItem(EntityPickupItemEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        // Schedule next tick so inventory is updated
        scheduleCheck(player);
    }

    // ─── Drop items ───────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onDropItem(PlayerDropItemEvent event) {
        scheduleCheck(event.getPlayer());
    }

    // ─── Inventory click (crafting, chests, etc.) ─────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        scheduleCheck(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player player)) return;
        scheduleCheck(player);
    }

    // ─── Consume items ────────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onConsume(PlayerItemConsumeEvent event) {
        scheduleCheck(event.getPlayer());
    }

    // ─── Death / drop all ─────────────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        // Delay to let items drop
        Player player = event.getEntity();
        plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                checkPlayer(player), 2L);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerRespawnEvent event) {
        scheduleCheck(event.getPlayer());
    }

    // ─── Block break (ores etc.) ──────────────────────────────────────────────

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        scheduleCheck(event.getPlayer());
    }

    // ─── Helper ───────────────────────────────────────────────────────────────

    private void scheduleCheck(Player player) {
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> checkPlayer(player), 1L);
    }

    private void checkPlayer(Player player) {
        if (player == null || !player.isOnline()) return;
        plugin.getGameManager().onPlayerInventoryChange(player);
    }
}
