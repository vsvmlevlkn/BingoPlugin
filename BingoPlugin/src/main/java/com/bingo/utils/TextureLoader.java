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

    // Mapea nombre de material → textura real en el JAR
    private static final Map<String, String> TEXTURE_MAP = new HashMap<>();
    static {
        for (String w : new String[]{"spruce","jungle","acacia","dark_oak","mangrove","cherry","bamboo","crimson","warped"}) {
            TEXTURE_MAP.put(w + "_slab",           w + "_planks");
            TEXTURE_MAP.put(w + "_stairs",         w + "_planks");
            TEXTURE_MAP.put(w + "_button",         w + "_planks");
            TEXTURE_MAP.put(w + "_pressure_plate", w + "_planks");
            TEXTURE_MAP.put(w + "_door",           w + "_door_bottom");
            TEXTURE_MAP.put(w + "_trapdoor",       w + "_trapdoor");
        }
        TEXTURE_MAP.put("melon",              "melon_side");
        TEXTURE_MAP.put("pumpkin",            "pumpkin_side");
        TEXTURE_MAP.put("glowstone",          "glowstone");
        TEXTURE_MAP.put("soul_sand",          "soul_sand");
        TEXTURE_MAP.put("quartz_block",       "quartz_block_side");
        TEXTURE_MAP.put("iron_block",         "iron_block");
        TEXTURE_MAP.put("gold_block",         "gold_block");
        TEXTURE_MAP.put("honey_block",        "honey_block_side");
        TEXTURE_MAP.put("clay",               "clay");
        TEXTURE_MAP.put("glass",              "glass");
        TEXTURE_MAP.put("glowstone_dust",     "glowstone_dust");
        TEXTURE_MAP.put("redstone",           "redstone_dust_dot");
        TEXTURE_MAP.put("prismarine",         "prismarine");
        TEXTURE_MAP.put("prismarine_bricks",  "prismarine_bricks");
        TEXTURE_MAP.put("dark_prismarine",    "dark_prismarine");
        TEXTURE_MAP.put("sea_lantern",        "sea_lantern");
        TEXTURE_MAP.put("obsidian",           "obsidian");
        TEXTURE_MAP.put("soul_lantern",       "soul_lantern");
        TEXTURE_MAP.put("polished_basalt",    "polished_basalt_side");
        TEXTURE_MAP.put("polished_blackstone","polished_blackstone");
        TEXTURE_MAP.put("lantern",            "lantern");
        TEXTURE_MAP.put("torch",              "torch");
        TEXTURE_MAP.put("cactus",             "cactus_side");
        TEXTURE_MAP.put("bamboo",             "bamboo_stalk");
        TEXTURE_MAP.put("sugar_cane",         "sugar_cane");
        TEXTURE_MAP.put("pumpkin_pie",        "pumpkin_pie");
    }

    public static void load(File serverDir) {
        if (loaded) return;
        loaded = true;
        File jar = findClientJar(serverDir);
        if (jar == null) {
            LOG.warning("[BingoPlugin] Client jar no encontrado. Usando colores.");
            return;
        }
        try (ZipFile zip = new ZipFile(jar)) {
            int count = 0;
            for (Material mat : Material.values()) {
                String matName = mat.name().toLowerCase();
                // Buscar nombre alternativo en el mapa
                String texName = TEXTURE_MAP.getOrDefault(matName, matName);
                BufferedImage img = tryLoad(zip, texName);
                if (img == null) img = tryLoad(zip, matName); // fallback al nombre original
                if (img != null) {
                    cache.put(mat, cropAndScale(img));
                    count++;
                }
            }
            LOG.info("[BingoPlugin] " + count + " texturas cargadas.");
        } catch (Exception e) {
            LOG.warning("[BingoPlugin] Error: " + e.getMessage());
        }
    }

    private static BufferedImage tryLoad(ZipFile zip, String name) {
        String[] paths = {
            "assets/minecraft/textures/item/" + name + ".png",
            "assets/minecraft/textures/block/" + name + ".png",
            "assets/minecraft/textures/items/" + name + ".png"
        };
        for (String path : paths) {
            try {
                ZipEntry entry = zip.getEntry(path);
                if (entry != null) {
                    try (InputStream is = zip.getInputStream(entry)) {
                        BufferedImage img = ImageIO.read(is);
                        if (img != null) return img;
                    }
                }
            } catch (Exception ignored) {}
        }
        return null;
    }

    // Recorta al primer cuadro (para texturas animadas) y escala a SIZE x SIZE
    private static BufferedImage cropAndScale(BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        // Si es más alta que ancha (textura animada), tomar solo el primer frame
        if (h > w) h = w;
        BufferedImage scaled = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = scaled.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                           RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
        g.drawImage(img.getSubimage(0, 0, w, h), 0, 0, SIZE, SIZE, null);
        g.dispose();
        return scaled;
    }

    private static File findClientJar(File serverDir) {
        String[] paths = {
            "cache/downloads/client.jar",
            "bundler/client/client.jar",
            ".paper-bundler/client.jar",
            "cache/client.jar"
        };
        for (String path : paths) {
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
