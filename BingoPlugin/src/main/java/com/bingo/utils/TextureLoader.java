package com.bingo.utils;

import org.bukkit.Material;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TextureLoader {
    private static final Map<Material, BufferedImage> cache = new HashMap<>();
    private static boolean loaded = false;
    private static final Logger LOG = Logger.getLogger("BingoPlugin");
    private static final int SIZE = 16;
    private static final int FACE = 8;

    private static final Map<String, String> TEXTURE_MAP = new HashMap<>();
    static {
        for (String w : new String[]{"spruce","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped","pale_oak"}) {
            TEXTURE_MAP.put(w + "_slab",           w + "_planks");
            TEXTURE_MAP.put(w + "_stairs",         w + "_planks");
            TEXTURE_MAP.put(w + "_button",         w + "_planks");
            TEXTURE_MAP.put(w + "_pressure_plate", w + "_planks");
            TEXTURE_MAP.put(w + "_door",           w + "_door_bottom");
        }
        TEXTURE_MAP.put("melon",              "melon_side");
        TEXTURE_MAP.put("pumpkin",            "pumpkin_side");
        TEXTURE_MAP.put("quartz_block",       "quartz_block_side");
        TEXTURE_MAP.put("honey_block",        "honey_block_side");
        TEXTURE_MAP.put("polished_basalt",    "polished_basalt_side");
        TEXTURE_MAP.put("iron_block",         "iron_block");
        TEXTURE_MAP.put("gold_block",         "gold_block");
        TEXTURE_MAP.put("glowstone",          "glowstone");
        TEXTURE_MAP.put("soul_sand",          "soul_sand");
        TEXTURE_MAP.put("obsidian",           "obsidian");
        TEXTURE_MAP.put("clay",               "clay");
        TEXTURE_MAP.put("glass",              "glass");
        TEXTURE_MAP.put("prismarine",         "prismarine");
        TEXTURE_MAP.put("prismarine_bricks",  "prismarine_bricks");
        TEXTURE_MAP.put("dark_prismarine",    "dark_prismarine");
        TEXTURE_MAP.put("sea_lantern",        "sea_lantern");
        TEXTURE_MAP.put("polished_blackstone","polished_blackstone");
        TEXTURE_MAP.put("redstone",           "redstone_dust_dot");
        TEXTURE_MAP.put("glowstone_dust",     "glowstone_dust");
    }

    public static void load(File serverDir) {
        if (loaded) return;
        loaded = true;
        File jar = findClientJar(serverDir);
        if (jar == null) {
            LOG.warning("[BingoPlugin] Client jar no encontrado.");
            return;
        }
        try (ZipFile zip = new ZipFile(jar)) {
            int count = 0;
            for (Material mat : Material.values()) {
                String matName = mat.name().toLowerCase();
                String texName = TEXTURE_MAP.getOrDefault(matName, matName);
                BufferedImage img = tryLoad(zip, texName);
                if (img == null && !texName.equals(matName)) img = tryLoad(zip, matName);
                if (img != null) {
                    img = cropFirstFrame(img);
                    img = mat.isBlock() ? makeIsometric(img) : scaleImage(img, SIZE, SIZE);
                    cache.put(mat, img);
                    count++;
                }
            }
            LOG.info("[BingoPlugin] " + count + " texturas cargadas.");
        } catch (Exception e) {
            LOG.warning("[BingoPlugin] Error: " + e.getMessage());
        }
    }

    // ── Isometric renderer ────────────────────────────────────────────
    // Cube corners (screen pixels, 16x16 output):
    //   Top face:   A=(8,1)  B=(14,4)  C=(8,7)  D=(2,4)
    //   Left face:  D=(2,4)  C=(8,7)   G=(8,13) H=(2,10)
    //   Right face: C=(8,7)  B=(14,4)  F=(14,10) G=(8,13)
    private static BufferedImage makeIsometric(BufferedImage tex) {
        BufferedImage t = scaleImage(tex, FACE, FACE);
        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);

        for (int sy = 0; sy < SIZE; sy++) {
            for (int sx = 0; sx < SIZE; sx++) {

                // TOP FACE — P = (8,1) + s*(6,3) + t*(-6,3)
                float sT = ((sx - 8) / 6.0f + (sy - 1) / 3.0f) / 2.0f;
                float tT = ((sy - 1) / 3.0f - (sx - 8) / 6.0f) / 2.0f;
                if (sT >= 0 && sT <= 1 && tT >= 0 && tT <= 1) {
                    out.setRGB(sx, sy, t.getRGB(clamp((int)(sT * FACE), 0, FACE-1),
                                                clamp((int)(tT * FACE), 0, FACE-1)));
                    continue;
                }

                // LEFT FACE — P = (2,4) + s*(6,3) + t*(0,6)
                float sL = (sx - 2) / 6.0f;
                float tL = (sy - 4 - 3 * sL) / 6.0f;
                if (sL >= 0 && sL <= 1 && tL >= 0 && tL <= 1) {
                    out.setRGB(sx, sy, darken(t.getRGB(clamp((int)(sL * FACE), 0, FACE-1),
                                                       clamp((int)(tL * FACE), 0, FACE-1)), 0.75f));
                    continue;
                }

                // RIGHT FACE — P = (8,7) + s*(6,-3) + t*(0,6)
                float sR = (sx - 8) / 6.0f;
                float tR = (sy - 7 + 3 * sR) / 6.0f;
                if (sR >= 0 && sR <= 1 && tR >= 0 && tR <= 1) {
                    out.setRGB(sx, sy, darken(t.getRGB(clamp((int)(sR * FACE), 0, FACE-1),
                                                       clamp((int)(tR * FACE), 0, FACE-1)), 0.55f));
                }
            }
        }
        return out;
    }

    private static int darken(int argb, float f) {
        int a = (argb >> 24) & 0xFF;
        if (a == 0) return 0;
        return (a << 24)
             | (clamp((int)(((argb >> 16) & 0xFF) * f), 0, 255) << 16)
             | (clamp((int)(((argb >>  8) & 0xFF) * f), 0, 255) <<  8)
             |  clamp((int)( (argb & 0xFF)         * f), 0, 255);
    }

    private static int clamp(int v, int min, int max) {
        return Math.max(min, Math.min(max, v));
    }

    // ── Helpers ───────────────────────────────────────────────────────
    private static BufferedImage tryLoad(ZipFile zip, String name) {
        for (String path : new String[]{
                "assets/minecraft/textures/item/"  + name + ".png",
                "assets/minecraft/textures/block/" + name + ".png"}) {
            try {
                ZipEntry e = zip.getEntry(path);
                if (e != null) {
                    try (InputStream is = zip.getInputStream(e)) {
                        BufferedImage img = ImageIO.read(is);
                        if (img != null) return img;
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    private static BufferedImage cropFirstFrame(BufferedImage img) {
        int w = img.getWidth(), h = img.getHeight();
        return (h > w) ? img.getSubimage(0, 0, w, w) : img;
    }

    private static BufferedImage scaleImage(BufferedImage img, int w, int h) {
        BufferedImage out = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img, 0, 0, w, h, null);
        g.dispose();
        return out;
    }

    private static File findClientJar(File serverDir) {
        for (String path : new String[]{
                "cache/downloads/client.jar","bundler/client/client.jar",
                ".paper-bundler/client.jar","cache/client.jar"}) {
            File f = new File(serverDir, path);
            if (f.exists()) return f;
        }
        File cache = new File(serverDir, "cache");
        if (cache.exists()) {
            File[] jars = cache.listFiles(f -> f.getName().endsWith(".jar")
                                           && f.getName().contains("client"));
            if (jars != null && jars.length > 0) return jars[0];
        }
        return null;
    }

    public static BufferedImage getTexture(Material mat) {
        return cache.get(mat);
    }
}
