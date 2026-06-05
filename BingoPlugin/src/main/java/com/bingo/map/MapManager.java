package com.bingo.map;

import com.bingo.BingoPlugin;
import com.bingo.models.BingoCard;
import com.bingo.models.BingoTeam;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.bukkit.map.MapView;

import java.util.*;
import java.util.logging.Logger;

/**
 * Handles creating and distributing custom map items that render the Bingo card.
 */
public class MapManager {

    private static final Logger log = Logger.getLogger("BingoPlugin");

    private final BingoPlugin plugin;
    // teamName -> renderer (for dirty marking on updates)
    private final Map<String, BingoMapRenderer> renderers = new HashMap<>();
    // teamName -> map view id
    private final Map<String, Integer> mapIds = new HashMap<>();

    public MapManager(BingoPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Creates a custom map for the given team's card and gives a copy to every online member.
     */
    public void createAndDistribute(BingoTeam team) {
        BingoCard card = team.getCard();
        if (card == null) {
            log.warning("[BingoPlugin] Tried to create map for team " + team.getName() + " but card is null.");
            return;
        }

        World world = Bukkit.getWorlds().get(0);
        MapView view = Bukkit.createMap(world);
        view.setScale(MapView.Scale.NORMAL);
        view.setTrackingPosition(false);
        view.setUnlimitedTracking(false);

        // Remove default renderers
        view.getRenderers().forEach(view::removeRenderer);

        BingoMapRenderer renderer = new BingoMapRenderer(card);
        view.addRenderer(renderer);

        renderers.put(team.getName().toLowerCase(), renderer);
        mapIds.put(team.getName().toLowerCase(), (int) view.getId());

        // Give the map to all online members
        for (UUID uuid : team.getMembers()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null && player.isOnline()) {
                giveMapToPlayer(player, view, team.getName());
            }
        }
    }

    /**
     * Gives (or re-gives) the team map to a specific player.
     */
    public void giveMapToPlayer(Player player, String teamName) {
        Integer id = mapIds.get(teamName.toLowerCase());
        if (id == null) {
            player.sendMessage("§cNo hay mapa generado para tu equipo todavía.");
            return;
        }
        MapView view = Bukkit.getMap(id);
        if (view == null) {
            player.sendMessage("§cNo se encontró el mapa.");
            return;
        }
        giveMapToPlayer(player, view, teamName);
    }

    private void giveMapToPlayer(Player player, MapView view, String teamName) {
        ItemStack mapItem = new ItemStack(Material.FILLED_MAP);
        MapMeta meta = (MapMeta) mapItem.getItemMeta();
        if (meta != null) {
            meta.setMapView(view);
            meta.setDisplayName("§6§lCartón de Bingo - §e" + teamName);
            List<String> lore = new ArrayList<>();
            lore.add("§7Consigue los objetos de tu cartón.");
            lore.add("§7Completa una fila, columna o diagonal.");
            meta.setLore(lore);
            mapItem.setItemMeta(meta);
        }
        // Give to offhand if available, otherwise first available slot
        if (player.getInventory().getItemInOffHand().getType() == Material.AIR) {
            player.getInventory().setItemInOffHand(mapItem);
        } else {
            player.getInventory().addItem(mapItem);
        }
        player.sendMessage("§a¡Has recibido tu cartón de Bingo!");
    }

    /**
     * Triggers a redraw of the map for the given team.
     * Call this after card state changes.
     */
    public void refreshMap(String teamName) {
        BingoMapRenderer renderer = renderers.get(teamName.toLowerCase());
        if (renderer != null) {
            renderer.markDirty();
        }
    }

    /** Clears all map data (call on stop/reroll). */
    public void clearAll() {
        renderers.clear();
        mapIds.clear();
    }
}
