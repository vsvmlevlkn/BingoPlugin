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
                String name = mat.name().toLowerCase();
                ZipEntry entry = zip.getEntry("assets/minecraft/textures/item/" + name + ".png");
                if (entry == null)
                    entry = zip.getEntry("assets/minecraft/textures/block/" + name + ".png");
                if (entry != null) {
                    try (InputStream is = zip.getInputStream(entry)) {
                        BufferedImage img = ImageIO.read(is);
                        if (img != null) {
                            BufferedImage scaled = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = scaled.createGraphics();
                            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
                            g.drawImage(img, 0, 0, SIZE, SIZE, null);
                            g.dispose();
                            cache.put(mat, scaled);
                            count++;
                        }
                    } catch (Exception ignored) {}
                }
            }
            LOG.info("[BingoPlugin] " + count + " texturas cargadas.");
        } catch (Exception e) {
            LOG.warning("[BingoPlugin] Error cargando texturas: " + e.getMessage());
        }
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
        // Buscar en cache/
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
