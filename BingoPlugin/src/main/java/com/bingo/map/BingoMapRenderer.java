package com.bingo.map;

import com.bingo.models.BingoCard;
import com.bingo.models.BingoItem;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapFont;
import org.bukkit.map.MapPalette;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.*;

/**
 * Renders the 5x5 Bingo card onto a Minecraft map.
 * The map is 128x128 pixels.
 * Each cell is ~24x24 pixels with a 2px border.
 *
 * Colour legend:
 *   - Incomplete cell: dark gray background, white text
 *   - Completed cell:  green background, black text
 *   - Header bar:      blue with team name
 */
public class BingoMapRenderer extends MapRenderer {

    private static final int MAP_SIZE = 128;
    private static final int HEADER_HEIGHT = 10;
    private static final int GRID_TOP = HEADER_HEIGHT + 2;
    private static final int CELL_SIZE = (MAP_SIZE - GRID_TOP) / 5; // ~23px
    private static final int PADDING = 1;

    // Map palette colours (closest approximations)
    private static final byte COLOR_BG         = MapPalette.matchColor(30, 30, 30);
    private static final byte COLOR_CELL_EMPTY = MapPalette.matchColor(60, 60, 80);
    private static final byte COLOR_CELL_DONE  = MapPalette.matchColor(34, 139, 34);
    private static final byte COLOR_HEADER     = MapPalette.matchColor(25, 60, 160);
    private static final byte COLOR_BORDER     = MapPalette.matchColor(10, 10, 10);
    private static final byte COLOR_WHITE      = MapPalette.matchColor(240, 240, 240);
    private static final byte COLOR_BLACK      = MapPalette.matchColor(10, 10, 10);
    private static final byte COLOR_YELLOW     = MapPalette.matchColor(255, 230, 50);

    private final BingoCard card;
    private boolean needsRedraw = true;

    public BingoMapRenderer(BingoCard card) {
        super(false); // contextual = false: renders once per map view, not per player
        this.card = card;
    }

    /** Call this when the card state changes to trigger a re-render. */
    public void markDirty() {
        needsRedraw = true;
    }

    @Override
    public void render(MapView map, MapCanvas canvas, Player player) {
        if (!needsRedraw) return;
        needsRedraw = false;

        // Background fill
        fillRect(canvas, 0, 0, MAP_SIZE, MAP_SIZE, COLOR_BG);

        // Header
        fillRect(canvas, 0, 0, MAP_SIZE, HEADER_HEIGHT, COLOR_HEADER);
        String header = "BINGO - " + card.getTeamName().toUpperCase();
        drawTextCentered(canvas, header, MAP_SIZE / 2, 2, COLOR_WHITE);

        // Draw grid
        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                drawCell(canvas, row, col);
            }
        }
    }

    private void drawCell(MapCanvas canvas, int row, int col) {
        BingoItem item = card.getItem(row, col);
        boolean done = card.isCompleted(row, col);

        int x = col * CELL_SIZE;
        int y = GRID_TOP + row * CELL_SIZE;
        int w = CELL_SIZE;
        int h = CELL_SIZE;

        // Border
        fillRect(canvas, x, y, w, h, COLOR_BORDER);

        // Cell background
        byte bg = done ? COLOR_CELL_DONE : COLOR_CELL_EMPTY;
        fillRect(canvas, x + PADDING, y + PADDING, w - PADDING * 2, h - PADDING * 2, bg);

        if (item == null) return;

        // Completed checkmark
        if (done) {
            drawTextCentered(canvas, "✓", x + w / 2, y + 2, COLOR_WHITE);
        }

        // Item name (wrapped into 2 lines max)
        byte textColor = done ? COLOR_WHITE : COLOR_WHITE;
        String name = item.getDisplayName();
        String[] lines = wrapText(name, 4); // ~4 chars per line at font scale
        int lineY = y + (done ? h / 2 : h / 3);
        for (String line : lines) {
            if (lineY + 5 < y + h) {
                drawTextCentered(canvas, line, x + w / 2, lineY, textColor);
                lineY += 6;
            }
        }

        // Difficulty dots at bottom-right
        String dots = "★".repeat(item.getDifficulty());
        drawText(canvas, dots, x + PADDING, y + h - 6, COLOR_YELLOW);
    }

    // ─── Drawing Helpers ──────────────────────────────────────────────────────

    private void fillRect(MapCanvas canvas, int x, int y, int w, int h, byte color) {
        for (int dx = 0; dx < w; dx++) {
            for (int dy = 0; dy < h; dy++) {
                int px = x + dx;
                int py = y + dy;
                if (px >= 0 && px < MAP_SIZE && py >= 0 && py < MAP_SIZE) {
                    canvas.setPixel(px, py, color);
                }
            }
        }
    }

    /**
     * Draws text using the built-in MinecraftFont.
     * Each character is ~6px wide, 8px tall.
     */
    private void drawText(MapCanvas canvas, String text, int x, int y, byte color) {
        try {
            canvas.drawText(x, y, MinecraftFont.Font, text);
        } catch (Exception ignored) {
            // Font doesn't support all characters – silently skip
        }
    }

    private void drawTextCentered(MapCanvas canvas, String text, int centerX, int y, byte color) {
        int textWidth = text.length() * 5; // approximate
        int x = centerX - textWidth / 2;
        drawText(canvas, text, Math.max(0, x), y, color);
    }

    /** Splits a string into lines of at most maxChars characters. */
    private String[] wrapText(String text, int maxChars) {
        // For map rendering we want short lines
        // Use 8 chars max per line given the small cell size
        int lineLen = 8;
        if (text.length() <= lineLen) return new String[]{text};

        // Try to split at a space
        int mid = text.lastIndexOf(' ', lineLen);
        if (mid <= 0) mid = lineLen;
        String line1 = text.substring(0, mid).trim();
        String line2 = text.substring(mid).trim();
        if (line2.length() > lineLen) line2 = line2.substring(0, lineLen - 1) + ".";
        return new String[]{line1, line2};
    }
}
