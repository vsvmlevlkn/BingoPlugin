package com.bingo.listeners;

import com.bingo.BingoPlugin;
import com.bingo.models.BingoTeam;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Handles player join/quit events.
 * On join: if game is running and player is in a team, give them their map.
 */
public class PlayerListener implements Listener {

    private final BingoPlugin plugin;

    public PlayerListener(BingoPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (!plugin.getGameManager().isGameRunning()) return;

        BingoTeam team = plugin.getTeamManager().getTeamByPlayer(event.getPlayer().getUniqueId());
        if (team == null) return;

        // Give map on next tick so inventory is ready
        plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
            if (event.getPlayer().isOnline() && team.getCard() != null) {
                plugin.getGameManager().getMapManager()
                        .giveMapToPlayer(event.getPlayer(), team.getName());
            }
        }, 20L);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        // If a player leaves, their items are gone from the team inventory.
        // Trigger a re-check for the other team member.
        if (!plugin.getGameManager().isGameRunning()) return;

        BingoTeam team = plugin.getTeamManager().getTeamByPlayer(event.getPlayer().getUniqueId());
        if (team == null) return;

        // Check remaining online members
        for (java.util.UUID uid : team.getMembers()) {
            if (!uid.equals(event.getPlayer().getUniqueId())) {
                org.bukkit.entity.Player other = org.bukkit.Bukkit.getPlayer(uid);
                if (other != null && other.isOnline()) {
                    plugin.getServer().getScheduler().runTaskLater(plugin, () ->
                            plugin.getGameManager().onPlayerInventoryChange(other), 2L);
                }
            }
        }
    }
}
