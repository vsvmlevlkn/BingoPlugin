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

    private static final Map<String, String> TEXTURE_MAP = new HashMap<>();
    static {
        for (String w : new String[]{"spruce","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped","pale_oak"}) {
            TEXTURE_MAP.put(w + "_slab",            w + "_planks");
            TEXTURE_MAP.put(w + "_stairs",          w + "_planks");
            TEXTURE_MAP.put(w + "_button",          w + "_planks");
            TEXTURE_MAP.put(w + "_pressure_plate",  w + "_planks");
            TEXTURE_MAP.put(w + "_door",            w + "_door_top");
            // trapdoors: usan su propia textura, no necesitan override
        }
        TEXTURE_MAP.put("melon",               "melon_side");
        TEXTURE_MAP.put("pumpkin",             "pumpkin_side");
        TEXTURE_MAP.put("quartz_block",        "quartz_block_side");
        TEXTURE_MAP.put("honey_block",         "honey_block_side");
        TEXTURE_MAP.put("polished_basalt",     "polished_basalt_side");
        TEXTURE_MAP.put("iron_block",          "iron_block");
        TEXTURE_MAP.put("gold_block",          "gold_block");
        TEXTURE_MAP.put("glowstone",           "glowstone");
        TEXTURE_MAP.put("soul_sand",           "soul_sand");
        TEXTURE_MAP.put("obsidian",            "obsidian");
        TEXTURE_MAP.put("clay",                "clay");
        TEXTURE_MAP.put("glass",               "glass");
        TEXTURE_MAP.put("prismarine",          "prismarine");
        TEXTURE_MAP.put("prismarine_bricks",   "prismarine_bricks");
        TEXTURE_MAP.put("dark_prismarine",     "dark_prismarine");
        TEXTURE_MAP.put("sea_lantern",         "sea_lantern");
        TEXTURE_MAP.put("polished_blackstone", "polished_blackstone");
        TEXTURE_MAP.put("redstone",            "redstone_dust_dot");
        TEXTURE_MAP.put("glowstone_dust",      "glowstone_dust");
        TEXTURE_MAP.put("cactus",              "cactus_side");
        TEXTURE_MAP.put("bamboo",              "bamboo_stalk");
    }

    public static void load(File serverDir) {
        if (loaded) return;
        loaded = true;
        File jar = findClientJar(serverDir);
        if (jar == null) { LOG.warning("[BingoPlugin] Client jar no encontrado."); return; }
        try (ZipFile zip = new ZipFile(jar)) {
            int count = 0;
            for (Material mat : Material.values()) {
                String matName = mat.name().toLowerCase();
                String texName = TEXTURE_MAP.getOrDefault(matName, matName);
                BufferedImage img = tryLoad(zip, texName);
                if (img == null && !texName.equals(matName)) img = tryLoad(zip, matName);
                if (img != null) {
                    img = cropFirstFrame(img);
                    img = applyRender(img, matName);
                    cache.put(mat, img);
                    count++;
                }
            }
            LOG.info("[BingoPlugin] " + count + " texturas cargadas.");
        } catch (Exception e) {
            LOG.warning("[BingoPlugin] Error: " + e.getMessage());
        }
    }

    private static BufferedImage applyRender(BufferedImage img, String name) {
        if (name.endsWith("_stairs"))         return renderStairs(img);
        if (name.endsWith("_slab"))           return renderSlab(img);
        if (name.endsWith("_trapdoor"))       return renderFlat(img);
        if (name.endsWith("_door"))           return renderFlat(img); // ya cargó _door_top
        if (name.endsWith("_pressure_plate")) return renderPlate(img);
        if (name.endsWith("_button"))         return renderPlate(img);
        return renderFlat(img);
    }

    // Escalera: lateral con forma de escalón (parte inferior completa + parte superior derecha)
    private static BufferedImage renderStairs(BufferedImage tex) {
        BufferedImage t = scaleImage(tex, SIZE, SIZE);
        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 0; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                if (y >= 8 || x >= 8)
                    out.setRGB(x, y, t.getRGB(x, y));
        return out;
    }

    // Losa: solo la mitad inferior de la textura
    private static BufferedImage renderSlab(BufferedImage tex) {
        BufferedImage t = scaleImage(tex, SIZE, SIZE);
        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 8; y < SIZE; y++)
            for (int x = 0; x < SIZE; x++)
                out.setRGB(x, y, t.getRGB(x, y));
        return out;
    }

    // Placa / botón: textura con 2 píxeles de borde eliminados
    private static BufferedImage renderPlate(BufferedImage tex) {
        BufferedImage t = scaleImage(tex, SIZE, SIZE);
        BufferedImage out = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        for (int y = 2; y < SIZE - 2; y++)
            for (int x = 2; x < SIZE - 2; x++)
                out.setRGB(x, y, t.getRGB(x, y));
        return out;
    }

    // Textura plana (trampillas, puertas top, bloques normales, items)
    private static BufferedImage renderFlat(BufferedImage tex) {
        return scaleImage(tex, SIZE, SIZE);
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
                "cache/downloads/client.jar", "bundler/client/client.jar",
                ".paper-bundler/client.jar",  "cache/client.jar"}) {
            File f = new File(serverDir, path);
            if (f.exists()) return f;
        }
        File cacheDir = new File(serverDir, "cache");
        if (cacheDir.exists()) {
            File[] jars = cacheDir.listFiles(f ->
                f.getName().endsWith(".jar") && f.getName().contains("client"));
            if (jars != null && jars.length > 0) return jars[0];
        }
        return null;
    }

    public static BufferedImage getTexture(Material mat) {
        return cache.get(mat);
    }
}
