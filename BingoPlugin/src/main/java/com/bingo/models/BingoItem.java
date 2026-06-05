package com.bingo.models;

import org.bukkit.Material;

/**
 * Represents a single Bingo card item with its difficulty score.
 */
public class BingoItem {

    private final Material material;
    private final String displayName;
    private final int difficulty;
    private final boolean netherRelated;

    public BingoItem(Material material, String displayName, int difficulty, boolean netherRelated) {
        this.material = material;
        this.displayName = displayName;
        this.difficulty = difficulty;
        this.netherRelated = netherRelated;
    }

    public BingoItem(Material material, String displayName, int difficulty) {
        this(material, displayName, difficulty, false);
    }

    public Material getMaterial() {
        return material;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public boolean isNetherRelated() {
        return netherRelated;
    }

    @Override
    public String toString() {
        return displayName + " (" + difficulty + "pts)";
    }
}
