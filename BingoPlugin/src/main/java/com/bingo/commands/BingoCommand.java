package com.bingo.commands;

import com.bingo.BingoPlugin;
import com.bingo.managers.GameManager;
import com.bingo.models.BingoCard;
import com.bingo.models.BingoItem;
import com.bingo.models.BingoTeam;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Handles all /bingo commands.
 *
 * Admin commands (op):
 *   /bingo start
 *   /bingo stop
 *   /bingo reload
 *   /bingo reroll
 *
 * Player commands:
 *   /bingo team <nombre>
 *   /bingo board
 *   /bingo map
 */
public class BingoCommand implements CommandExecutor, TabCompleter {

    private final BingoPlugin plugin;

    private static final List<String> ADMIN_SUBS = Arrays.asList("start", "stop", "reload", "reroll");
    private static final List<String> PLAYER_SUBS = Arrays.asList("team", "board", "map");

    public BingoCommand(BingoPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {

            // ─── Admin Commands ───────────────────────────────────────────────
            case "start" -> {
                if (!isAdmin(sender)) return true;
                if (plugin.getGameManager().isGameRunning()) {
                    sender.sendMessage(colorize(plugin.getConfig()
                            .getString("messages.game-already-running", "&cYa hay una partida en curso.")));
                    return true;
                }
                boolean ok = plugin.getGameManager().startGame();
                if (!ok) {
                    sender.sendMessage("§cNo hay equipos con jugadores. Usa §e/bingo team <nombre> §cprimero.");
                }
            }
            case "stop" -> {
                if (!isAdmin(sender)) return true;
                plugin.getGameManager().stopGame();
            }
            case "reload" -> {
                if (!isAdmin(sender)) return true;
                plugin.getGameManager().reloadConfig();
            }
            case "reroll" -> {
                if (!isAdmin(sender)) return true;
                plugin.getGameManager().rerollCards();
            }

            // ─── Player Commands ──────────────────────────────────────────────
            case "team" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cEste comando solo puede usarse como jugador.");
                    return true;
                }
                if (args.length < 2) {
                    sender.sendMessage("§cUso: /bingo team <nombre>");
                    return true;
                }
                String teamName = args[1];
                boolean joined = plugin.getTeamManager().assignPlayer(player, teamName);
                if (joined) {
                    String msg = colorize(plugin.getConfig()
                            .getString("messages.team-joined", "&aOs habéis unido al equipo &e%team%&a!")
                            .replace("%team%", teamName));
                    player.sendMessage(msg);
                } else {
                    // Check if team exists vs is full
                    BingoTeam t = plugin.getTeamManager().getTeam(teamName);
                    if (t == null) {
                        player.sendMessage("§cEse equipo no existe. Equipos disponibles: " + getTeamList());
                    } else {
                        player.sendMessage(colorize(plugin.getConfig()
                                .getString("messages.team-full", "&cEse equipo ya está completo.")));
                    }
                }
            }
            case "board" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cEste comando solo puede usarse como jugador.");
                    return true;
                }
                showBoard(player);
            }
            case "map" -> {
                if (!(sender instanceof Player player)) {
                    sender.sendMessage("§cEste comando solo puede usarse como jugador.");
                    return true;
                }
                if (!plugin.getGameManager().isGameRunning()) {
                    player.sendMessage(colorize(plugin.getConfig()
                            .getString("messages.no-game", "&cNo hay ninguna partida activa.")));
                    return true;
                }
                BingoTeam team = plugin.getTeamManager().getTeamByPlayer(player.getUniqueId());
                if (team == null) {
                    player.sendMessage("§cNo perteneces a ningún equipo.");
                    return true;
                }
                plugin.getGameManager().getMapManager().giveMapToPlayer(player, team.getName());
            }
            default -> sendHelp(sender);
        }
        return true;
    }

    // ─── Board Display ─────────────────────────────────────────────────────────

    private void showBoard(Player player) {
        BingoTeam team = plugin.getTeamManager().getTeamByPlayer(player.getUniqueId());
        if (team == null) {
            player.sendMessage("§cNo perteneces a ningún equipo. Usa §e/bingo team <nombre>§c.");
            return;
        }
        if (!plugin.getGameManager().isGameRunning()) {
            player.sendMessage("§cLa partida no ha comenzado.");
            return;
        }
        BingoCard card = team.getCard();
        if (card == null) {
            player.sendMessage("§cTu equipo no tiene cartón asignado.");
            return;
        }

        player.sendMessage("");
        player.sendMessage("§6§l╔══ BINGO - Equipo " + team.getName() + " ══╗");
        player.sendMessage("§7Total puntos: §e" + card.getTotalPoints());
        player.sendMessage("");

        for (int row = 0; row < BingoCard.SIZE; row++) {
            StringBuilder line = new StringBuilder("§7│ ");
            for (int col = 0; col < BingoCard.SIZE; col++) {
                BingoItem item = card.getItem(row, col);
                boolean done = card.isCompleted(row, col);
                if (done) {
                    line.append("§a§l✓ ").append(item != null ? item.getDisplayName() : "?");
                } else {
                    line.append("§c✗ ").append(item != null ? item.getDisplayName() : "?");
                }
                if (col < BingoCard.SIZE - 1) line.append(" §7│ ");
            }
            line.append(" §7│");
            player.sendMessage(line.toString());
        }

        player.sendMessage("§6§l╚" + "═".repeat(30) + "╝");
        player.sendMessage("");

        // Completion summary
        int completed = 0;
        for (int r = 0; r < BingoCard.SIZE; r++)
            for (int c = 0; c < BingoCard.SIZE; c++)
                if (card.isCompleted(r, c)) completed++;

        player.sendMessage("§7Conseguidos: §a" + completed + "§7/§e25");
        player.sendMessage("");
    }

    // ─── Tab Completion ────────────────────────────────────────────────────────

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>(PLAYER_SUBS);
            if (isAdmin(sender)) completions.addAll(ADMIN_SUBS);
            List<String> result = new ArrayList<>();
            for (String s : completions) {
                if (s.startsWith(args[0].toLowerCase())) result.add(s);
            }
            return result;
        }
        if (args.length == 2 && args[0].equalsIgnoreCase("team")) {
            List<String> teams = plugin.getTeamManager().getTeamNames();
            List<String> result = new ArrayList<>();
            for (String t : teams) {
                if (t.startsWith(args[1].toLowerCase())) result.add(t);
            }
            return result;
        }
        return Collections.emptyList();
    }

    // ─── Helpers ──────────────────────────────────────────────────────────────

    private boolean isAdmin(CommandSender sender) {
        if (sender.hasPermission("bingo.admin") || sender.isOp()) return true;
        sender.sendMessage("§cNo tienes permiso para usar ese comando.");
        return false;
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage("§6§l--- BingoPlugin ---");
        if (isAdminSilent(sender)) {
            sender.sendMessage("§e/bingo start §7- Iniciar partida");
            sender.sendMessage("§e/bingo stop §7- Detener partida");
            sender.sendMessage("§e/bingo reload §7- Recargar config");
            sender.sendMessage("§e/bingo reroll §7- Nuevos cartones");
        }
        sender.sendMessage("§e/bingo team <nombre> §7- Unirse a un equipo");
        sender.sendMessage("§e/bingo board §7- Ver tu cartón en chat");
        sender.sendMessage("§e/bingo map §7- Recibir el mapa");
        sender.sendMessage("§7Equipos: §f" + getTeamList());
    }

    private boolean isAdminSilent(CommandSender sender) {
        return sender.hasPermission("bingo.admin") || sender.isOp();
    }

    private String getTeamList() {
        return String.join("§7, §e", plugin.getTeamManager().getTeamNames());
    }

    private static String colorize(String msg) {
        return msg.replace("&", "§");
    }
}
