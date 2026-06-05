package com.bingo.utils;

import com.bingo.models.BingoCard;
import com.bingo.models.BingoItem;

import java.util.*;
import java.util.logging.Logger;

/**
 * Generates balanced, random Bingo cards for each team.
 * Cards are balanced within 5 points of each other.
 * Nether items are capped at 3-5 per card.
 */
public class CardGenerator {

    private static final Logger log = Logger.getLogger("BingoPlugin");

    // Target total difficulty per card
    private static final int TARGET_POINTS = 60;
    private static final int MAX_DEVIATION = 5;
    private static final int NETHER_MIN = 3;
    private static final int NETHER_MAX = 5;
    private static final int CARD_SLOTS = 25;

    /**
     * Generates unique cards for the given team names.
     * Each card has 25 distinct items with balanced difficulty.
     *
     * @param teamNames names of all teams
     * @return map of teamName -> BingoCard
     */
    public static Map<String, BingoCard> generateCards(List<String> teamNames) {
        Map<String, BingoCard> cards = new LinkedHashMap<>();
        Set<org.bukkit.Material> usedGlobally = new HashSet<>();

        for (String team : teamNames) {
            BingoItem[][] grid = generateGrid(usedGlobally);
            // Register materials used by this team so others don't get the same items
            for (BingoItem[] row : grid) {
                for (BingoItem item : row) {
                    if (item != null) usedGlobally.add(item.getMaterial());
                }
            }
            cards.put(team, new BingoCard(team, grid));
        }
        return cards;
    }

    /**
     * Generates a single 5x5 grid of BingoItems.
     * Uses a difficulty-slot strategy:
     *  - Fills specific difficulty counts to reach ~60 points
     *  - Ensures NETHER_MIN to NETHER_MAX nether items
     */
    private static BingoItem[][] generateGrid(Set<org.bukkit.Material> excludedMaterials) {
        Random rng = new Random();
        List<BingoItem> selected = null;

        for (int attempt = 0; attempt < 50; attempt++) {
            List<BingoItem> candidate = buildCandidateList(rng, excludedMaterials);
            if (candidate != null && candidate.size() == CARD_SLOTS) {
                int total = candidate.stream().mapToInt(BingoItem::getDifficulty).sum();
                if (Math.abs(total - TARGET_POINTS) <= MAX_DEVIATION) {
                    selected = candidate;
                    break;
                }
            }
        }

        // Fallback: just pick any valid 25 items
        if (selected == null) {
            selected = fallbackSelection(rng, excludedMaterials);
        }

        Collections.shuffle(selected, rng);
        BingoItem[][] grid = new BingoItem[5][5];
        for (int i = 0; i < 25; i++) {
            grid[i / 5][i % 5] = selected.get(i);
        }
        return grid;
    }

    /**
     * Strategy: pick items from each difficulty tier in specific quantities
     * to land near TARGET_POINTS (60).
     *
     * Slot distribution to reach ~60 pts:
     *   8 x 1pt = 8
     *   6 x 2pt = 12
     *   5 x 3pt = 15
     *   4 x 4pt = 16
     *   2 x 5pt = 10
     *   Total  = 61 pts (within range)
     */
    private static List<BingoItem> buildCandidateList(Random rng, Set<org.bukkit.Material> excluded) {
        int[] counts = {8, 6, 5, 4, 2}; // items per difficulty level 1..5
        List<BingoItem> result = new ArrayList<>();

        for (int diff = 1; diff <= 5; diff++) {
            List<BingoItem> pool = ItemPool.getByDifficulty(diff);
            // Remove excluded materials
            pool.removeIf(i -> excluded.contains(i.getMaterial()));
            // Also remove already-selected materials from this card
            Set<org.bukkit.Material> selected = new HashSet<>();
            result.forEach(i -> selected.add(i.getMaterial()));
            pool.removeIf(i -> selected.contains(i.getMaterial()));

            int needed = counts[diff - 1];
            if (pool.size() < needed) {
                return null; // Not enough items available
            }
            Collections.shuffle(pool, rng);
            result.addAll(pool.subList(0, needed));
        }

        // Enforce nether item count
        long netherCount = result.stream().filter(BingoItem::isNetherRelated).count();
        if (netherCount < NETHER_MIN || netherCount > NETHER_MAX) {
            return null;
        }

        return result;
    }

    private static List<BingoItem> fallbackSelection(Random rng, Set<org.bukkit.Material> excluded) {
        List<BingoItem> all = ItemPool.getAll();
        all.removeIf(i -> excluded.contains(i.getMaterial()));
        Collections.shuffle(all, rng);
        List<BingoItem> result = new ArrayList<>();
        Set<org.bukkit.Material> used = new HashSet<>();
        for (BingoItem item : all) {
            if (!used.contains(item.getMaterial())) {
                result.add(item);
                used.add(item.getMaterial());
                if (result.size() == CARD_SLOTS) break;
            }
        }
        log.warning("[BingoPlugin] Used fallback card generation - nether balance may not be perfect.");
        return result;
    }
}
