package com.bingo.managers;

import com.bingo.BingoPlugin;
import com.bingo.map.MapManager;
import com.bingo.models.BingoCard;
import com.bingo.models.BingoTeam;
import com.bingo.utils.CardGenerator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Logger;

/**
 * Central manager for the Bingo game state.
 * Handles start, stop, reroll, progress tracking, and win detection.
 */
public class GameManager {

    private static final Logger log = Logger.getLogger("BingoPlugin");

    private final BingoPlugin plugin;
    private final MapManager mapManager;
    private boolean gameRunning = false;

    public GameManager(BingoPlugin plugin) {
        this.plugin = plugin;
        this.mapManager = new MapManager(plugin);
    }

    // ─── Game Lifecycle ───────────────────────────────────────────────────────

    public boolean startGame() {
        if (gameRunning) return false;

        List<BingoTeam> activeTeams = plugin.getTeamManager().getActiveTeams();
        if (activeTeams.isEmpty()) {
            return false;
        }

        // Generate cards
        List<String> teamNames = new ArrayList<>();
        for (BingoTeam t : activeTeams) teamNames.add(t.getName());

        Map<String, BingoCard> cards = CardGenerator.generateCards(teamNames);

        // Assign cards and create maps
        for (BingoTeam team : activeTeams) {
            BingoCard card = cards.get(team.getName());
            if (card == null) continue;
            team.setCard(card);
            mapManager.createAndDistribute(team);
        }

        gameRunning = true;

        // Announce start
        String msg = colorize(plugin.getConfig().getString("messages.game-start",
                "&a¡La partida de Bingo ha comenzado!"));
        Bukkit.broadcastMessage(msg);

        broadcastTitle("§6§lBINGO", "§e¡Que empiece el juego!", 20, 60, 20);
        Bukkit.getOnlinePlayers().forEach(p ->
                p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1f));

        return true;
    }

    public void stopGame() {
        gameRunning = false;
        mapManager.clearAll();
        plugin.getTeamManager().clearCards();

        String msg = colorize(plugin.getConfig().getString("messages.game-stop",
                "&cLa partida ha sido detenida."));
        Bukkit.broadcastMessage(msg);
    }

    public void rerollCards() {
        boolean wasRunning = gameRunning;
        if (wasRunning) stopGame();

        mapManager.clearAll();
        plugin.getTeamManager().clearCards();

        if (wasRunning) startGame();
        else {
            String msg = colorize(plugin.getConfig().getString("messages.game-reroll",
                    "&eNuevos cartones generados!"));
            Bukkit.broadcastMessage(msg);
        }
    }

    public void reloadConfig() {
        plugin.reloadConfig();
        String msg = colorize(plugin.getConfig().getString("messages.game-reload",
                "&aConfiguración recargada."));
        Bukkit.broadcastMessage(msg);
    }

    // ─── Progress Tracking ────────────────────────────────────────────────────

    /**
     * Called from InventoryListener whenever a player's inventory changes.
     * Updates the team card and checks for win condition.
     */
    public void onPlayerInventoryChange(Player player) {
        if (!gameRunning) return;

        BingoTeam team = plugin.getTeamManager().getTeamByPlayer(player.getUniqueId());
        if (team == null || team.getCard() == null) return;

        BingoCard card = team.getCard();
        Set<Material> teamInventory = team.collectTeamInventory();

        // Track which items changed
        List<String> newlyCompleted = new ArrayList<>();
        List<String> newlyLost = new ArrayList<>();

        for (int r = 0; r < BingoCard.SIZE; r++) {
            for (int c = 0; c < BingoCard.SIZE; c++) {
                var item = card.getItem(r, c);
                if (item == null) continue;
                boolean before = card.isCompleted(r, c);
                boolean nowHas = teamInventory.contains(item.getMaterial());

                if (card.setCompleted(r, c, nowHas)) {
                    if (nowHas && !before) {
                        newlyCompleted.add(item.getDisplayName());
                    } else if (!nowHas && before) {
                        newlyLost.add(item.getDisplayName());
                    }
                }
            }
        }

        if (!newlyCompleted.isEmpty() || !newlyLost.isEmpty()) {
            mapManager.refreshMap(team.getName());

            for (String itemName : newlyCompleted) {
                String msg = colorize(plugin.getConfig()
                        .getString("messages.item-completed", "&a¡El equipo &e%team% &aconsiguió &e%item%&a!")
                        .replace("%team%", team.getName())
                        .replace("%item%", itemName));
                Bukkit.broadcastMessage(msg);

                // Notify team members with action bar
                for (UUID uid : team.getMembers()) {
                    Player tp = Bukkit.getPlayer(uid);
                    if (tp != null && tp.isOnline()) {
                        tp.playSound(tp.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1.5f);
                        tp.spigot().sendMessage(ChatMessageType.ACTION_BAR,
                                new TextComponent("§a✓ " + itemName + " conseguido!"));
                    }
                }
            }

            // Check win after updating
            if (card.hasWon()) {
                handleWin(team);
            }
        }
    }

    // ─── Win Handling ─────────────────────────────────────────────────────────

    private void handleWin(BingoTeam winningTeam) {
        gameRunning = false;

        String msg = colorize(plugin.getConfig()
                .getString("messages.bingo-win", "&6&l¡BINGO! &eEl equipo &6%team% &eha ganado!")
                .replace("%team%", winningTeam.getName()));

        Bukkit.broadcastMessage("");
        Bukkit.broadcastMessage("§6§l" + "═".repeat(40));
        Bukkit.broadcastMessage(msg);
        Bukkit.broadcastMessage("§6§l" + "═".repeat(40));
        Bukkit.broadcastMessage("");

        // Title for all players
        broadcastTitle(
                "§6§l¡BINGO!",
                "§eGanador: §6§l" + winningTeam.getName(),
                10, 80, 20
        );

        // Sound for all players
        Bukkit.getOnlinePlayers().forEach(p -> {
            p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 0.8f);
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_BLAST, 1f, 1f);
            p.playSound(p.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
        });

        // Schedule cleanup
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            mapManager.clearAll();
            plugin.getTeamManager().clearCards();
        }, 200L); // 10 seconds after win
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    public boolean isGameRunning() {
        return gameRunning;
    }

    public MapManager getMapManager() {
        return mapManager;
    }

    private void broadcastTitle(String title, String subtitle, int fadeIn, int stay, int fadeOut) {
        Bukkit.getOnlinePlayers().forEach(p ->
                p.sendTitle(title, subtitle, fadeIn, stay, fadeOut));
    }

    public static String colorize(String msg) {
        return msg.replace("&", "§");
    }
}
