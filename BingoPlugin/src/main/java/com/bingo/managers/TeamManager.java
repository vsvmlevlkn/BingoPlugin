package com.bingo.managers;

import com.bingo.BingoPlugin;
import com.bingo.models.BingoTeam;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manages all Bingo teams. Handles creation, player assignment, and lookups.
 */
public class TeamManager {

    private final BingoPlugin plugin;
    private final Map<String, BingoTeam> teams;

    // Predefined team colors/names for easy setup
    private static final String[] DEFAULT_TEAMS = {
            "Rojo", "Azul", "Verde", "Amarillo", "Morado", "Naranja"
    };

    public TeamManager(BingoPlugin plugin) {
        this.plugin = plugin;
        this.teams = new LinkedHashMap<>();
        initDefaultTeams();
    }

    private void initDefaultTeams() {
        for (String name : DEFAULT_TEAMS) {
            teams.put(name.toLowerCase(), new BingoTeam(name, plugin.getConfig().getInt("team-size", 2)));
        }
    }

    /**
     * Assigns a player to a team by name.
     * Removes them from any previous team first.
     */
    public boolean assignPlayer(Player player, String teamName) {
        String key = teamName.toLowerCase();
        if (!teams.containsKey(key)) {
            return false;
        }
        BingoTeam target = teams.get(key);
        if (target.isFull() && !target.hasMember(player.getUniqueId())) {
            return false;
        }

        // Remove from current team
        removePlayerFromAllTeams(player.getUniqueId());

        return target.addMember(player.getUniqueId());
    }

    public void removePlayerFromAllTeams(UUID uuid) {
        for (BingoTeam team : teams.values()) {
            team.removeMember(uuid);
        }
    }

    public BingoTeam getTeamByPlayer(UUID uuid) {
        for (BingoTeam team : teams.values()) {
            if (team.hasMember(uuid)) return team;
        }
        return null;
    }

    public BingoTeam getTeam(String name) {
        return teams.get(name.toLowerCase());
    }

    /** Returns only teams that have at least one member. */
    public List<BingoTeam> getActiveTeams() {
        List<BingoTeam> active = new ArrayList<>();
        for (BingoTeam team : teams.values()) {
            if (team.getSize() > 0) active.add(team);
        }
        return active;
    }

    public Collection<BingoTeam> getAllTeams() {
        return teams.values();
    }

    public List<String> getTeamNames() {
        return new ArrayList<>(teams.keySet());
    }

    /** Resets all teams (clears members and cards). Called on reroll/stop. */
    public void resetTeams() {
        int teamSize = plugin.getConfig().getInt("team-size", 2);
        teams.clear();
        for (String name : DEFAULT_TEAMS) {
            teams.put(name.toLowerCase(), new BingoTeam(name, teamSize));
        }
    }

    /** Clears cards only, keeps team members. */
    public void clearCards() {
        for (BingoTeam team : teams.values()) {
            team.setCard(null);
        }
    }
}
