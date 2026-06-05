package com.bingo.models;

import org.bukkit.entity.Player;

import java.util.*;

/**
 * Represents a Bingo team with up to 2 players and their shared card.
 */
public class BingoTeam {

    private final String name;
    private final List<UUID> members;
    private BingoCard card;
    private final int maxSize;

    public BingoTeam(String name, int maxSize) {
        this.name = name;
        this.maxSize = maxSize;
        this.members = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public List<UUID> getMembers() {
        return Collections.unmodifiableList(members);
    }

    public boolean addMember(UUID uuid) {
        if (members.size() >= maxSize || members.contains(uuid)) {
            return false;
        }
        members.add(uuid);
        return true;
    }

    public boolean removeMember(UUID uuid) {
        return members.remove(uuid);
    }

    public boolean isFull() {
        return members.size() >= maxSize;
    }

    public boolean hasMember(UUID uuid) {
        return members.contains(uuid);
    }

    public int getSize() {
        return members.size();
    }

    public BingoCard getCard() {
        return card;
    }

    public void setCard(BingoCard card) {
        this.card = card;
    }

    /**
     * Collects all materials held by any online team member.
     */
    public Set<org.bukkit.Material> collectTeamInventory() {
        Set<org.bukkit.Material> materials = new HashSet<>();
        for (UUID uuid : members) {
            Player player = org.bukkit.Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) continue;
            for (org.bukkit.inventory.ItemStack item : player.getInventory().getContents()) {
                if (item != null && item.getType() != org.bukkit.Material.AIR) {
                    materials.add(item.getType());
                }
            }
        }
        return materials;
    }
}
