package com.bingo.map;

import com.bingo.models.BingoCard;
import com.bingo.models.BingoItem;
import com.bingo.utils.TextureLoader;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MinecraftFont;

import java.awt.image.BufferedImage;

public class BingoMapRenderer extends MapRenderer {
    private final BingoCard card;
    private boolean dirty = true;

    public BingoMapRenderer(BingoCard card) {
        this.card = card;
    }

    public void markDirty() {
        this.dirty = true;
    }

    @Override
    public void render(MapView view, MapCanvas canvas, Player player) {
        if (!dirty) return;
        dirty = false;

        for (int x = 0; x < 128; x++)
            for (int y = 0; y < 128; y++)
                canvas.setPixel(x, y, (byte) 119);

        int cellSize = 24;
        int offsetX = 4;
        int offsetY = 8;

        for (int row = 0; row < 5; row++) {
            for (int col = 0; col < 5; col++) {
                int x = offsetX + col * cellSize;
                int y = offsetY + row * cellSize;
                BingoItem item = card.getItem(row, col);
                boolean completed = card.isCompleted(row, col);

                byte bgColor = completed ? (byte) 28 : (byte) 8;
                for (int dx = 1; dx < cellSize - 1; dx++)
                    for (int dy = 1; dy < cellSize - 1; dy++)
                        canvas.setPixel(x + dx, y + dy, bgColor);

                for (int dx = 0; dx < cellSize; dx++) {
                    canvas.setPixel(x + dx, y, (byte) 119);
                    canvas.setPixel(x + dx, y + cellSize - 1, (byte) 119);
                }
                for (int dy = 0; dy < cellSize; dy++) {
                    canvas.setPixel(x, y + dy, (byte) 119);
                    canvas.setPixel(x + cellSize - 1, y + dy, (byte) 119);
                }

                if (item != null) {
                    BufferedImage texture = TextureLoader.getTexture(item.getMaterial());
                    if (texture != null) {
                        canvas.drawImage(x + 4, y + 4, texture);
                    } else {
                        byte c = diffColor(item.getDifficulty());
                        for (int dx = 4; dx < cellSize - 4; dx++)
                            for (int dy = 4; dy < cellSize - 4; dy++)
                                canvas.setPixel(x + dx, y + dy, c);
                    }

                    if (completed) {
                        canvas.setPixel(x + 2, y + 5, (byte) 34);
                        canvas.setPixel(x + 3, y + 6, (byte) 34);
                        canvas.setPixel(x + 4, y + 5, (byte) 34);
                        canvas.setPixel(x + 5, y + 4, (byte) 34);
                        canvas.setPixel(x + 6, y + 3, (byte) 34);
                    }
                }
            }
        }

        canvas.drawText(38, 1, MinecraftFont.Font, "§fBINGO");
    }

    private byte diffColor(int diff) {
        return switch (diff) {
            case 1 -> (byte) 34;
            case 2 -> (byte) 50;
            case 3 -> (byte) 18;
            case 4 -> (byte) 2;
            default -> (byte) 8;
        };
    }
}
