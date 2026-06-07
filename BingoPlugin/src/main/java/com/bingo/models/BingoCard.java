package com.bingo.models;

public class BingoCard {
    public static final int SIZE = 5;
    private final BingoItem[][] items;
    private final boolean[][] completed;
    private final String teamName;
    private int totalPoints;

    public BingoCard(String teamName, BingoItem[][] items) {
        this.teamName = teamName;
        this.items = items;
        this.completed = new boolean[SIZE][SIZE];
        calculateTotalPoints();
    }

    private void calculateTotalPoints() {
        totalPoints = 0;
        for (int r = 0; r < SIZE; r++)
            for (int c = 0; c < SIZE; c++)
                if (items[r][c] != null)
                    totalPoints += items[r][c].getDifficulty();
    }

    public boolean setCompleted(int row, int col, boolean value) {
        if (completed[row][col] == value) return false;
        completed[row][col] = value;
        return true;
    }

    public boolean isCompleted(int row, int col) {
        return completed[row][col];
    }

    public BingoItem getItem(int row, int col) { return items[row][col]; }
    public BingoItem[][] getItems() { return items; }
    public boolean[][] getCompleted() { return completed; }
    public String getTeamName() { return teamName; }
    public int getTotalPoints() { return totalPoints; }

    public boolean hasWon() {
        return checkRows() || checkColumns() || checkDiagonals();
    }

    private boolean checkRows() {
        for (int r = 0; r < SIZE; r++) {
            boolean full = true;
            for (int c = 0; c < SIZE; c++) if (!completed[r][c]) { full = false; break; }
            if (full) return true;
        }
        return false;
    }

    private boolean checkColumns() {
        for (int c = 0; c < SIZE; c++) {
            boolean full = true;
            for (int r = 0; r < SIZE; r++) if (!completed[r][c]) { full = false; break; }
            if (full) return true;
        }
        return false;
    }

    private boolean checkDiagonals() {
        boolean main = true;
        for (int i = 0; i < SIZE; i++) if (!completed[i][i]) { main = false; break; }
        if (main) return true;
        boolean anti = true;
        for (int i = 0; i < SIZE; i++) if (!completed[i][SIZE - 1 - i]) { anti = false; break; }
        return anti;
    }

    public boolean updateFromInventory(java.util.Set<org.bukkit.Material> teamInventory) {
        boolean changed = false;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                BingoItem item = items[r][c];
                if (item == null) continue;
                boolean nowHas = teamInventory.contains(item.getMaterial());
                if (setCompleted(r, c, nowHas)) changed = true;
            }
        }
        return changed;
    }
}
