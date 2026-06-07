package com.bingo.utils;

import com.bingo.models.BingoItem;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

/**
 * Registry of all valid Bingo items, organized by difficulty.
 * Forbidden: End items, Netherite, Enchanted items, Silk-Touch-only ores.
 */
public class ItemPool {

    private static final List<BingoItem> ALL_ITEMS = new ArrayList<>();

    static {
 // ── DIFICULTAD 1 ──────────────────────────────────
        add(Material.BREAD,                   "Pan",                        1);
        add(Material.WHEAT,                   "Trigo",                      1);
        add(Material.EGG,                     "Huevo",                      1);
        add(Material.FEATHER,                 "Pluma",                      1);
        add(Material.BONE,                    "Hueso",                      1);
        add(Material.LEATHER,                 "Cuero",                      1);
        add(Material.CHARCOAL,                "Carbón vegetal",             1);
        add(Material.APPLE,                   "Manzana",                    1);
        add(Material.CACTUS,                  "Cactus",                     1);
        add(Material.BAMBOO,                  "Bambú",                      1);
        add(Material.PUMPKIN,                 "Calabaza",                   1);
        add(Material.MELON,                   "Melón",                      1);
        add(Material.CARROT,                  "Zanahoria",                  1);
        add(Material.POTATO,                  "Patata",                     1);
        add(Material.BEETROOT,                "Remolacha",                  1);
        add(Material.SWEET_BERRIES,           "Bayas dulces",               1);
        add(Material.SUGAR_CANE,              "Caña de azúcar",             1);
        add(Material.PUMPKIN_PIE,             "Tarta de calabaza",          1);
        add(Material.MUSHROOM_STEW,           "Sopa de champiñones",        1);
        add(Material.OAK_LOG,                 "Tronco de roble",            1);
        add(Material.FISHING_ROD,             "Caña de pescar",             1);
        add(Material.FLINT,                   "Pedernal",                   1);
        add(Material.INK_SAC,                 "Saco de tinta",              1);
        add(Material.COD,                     "Bacalao",                    1);
        add(Material.SALMON,                  "Salmón",                     1);
        add(Material.TROPICAL_FISH,           "Pez tropical",               1);
        add(Material.RABBIT_HIDE,             "Piel de conejo",             1);
        add(Material.RABBIT,                  "Conejo crudo",               1);
        add(Material.COOKED_RABBIT,           "Conejo asado",               1);
        // Abeto
        add(Material.SPRUCE_SLAB,             "Losa de abeto",              1);
        add(Material.SPRUCE_STAIRS,           "Escalera de abeto",          1);
        add(Material.SPRUCE_DOOR,             "Puerta de abeto",            1);
        add(Material.SPRUCE_TRAPDOOR,         "Trampilla de abeto",         1);
        add(Material.SPRUCE_BUTTON,           "Botón de abeto",             1);
        add(Material.SPRUCE_PRESSURE_PLATE,   "Placa de abeto",             1);
        // Jungla
        add(Material.JUNGLE_SLAB,             "Losa de jungla",             1);
        add(Material.JUNGLE_STAIRS,           "Escalera de jungla",         1);
        add(Material.JUNGLE_DOOR,             "Puerta de jungla",           1);
        add(Material.JUNGLE_TRAPDOOR,         "Trampilla de jungla",        1);
        add(Material.JUNGLE_BUTTON,           "Botón de jungla",            1);
        add(Material.JUNGLE_PRESSURE_PLATE,   "Placa de jungla",            1);
        // Acacia
        add(Material.ACACIA_SLAB,             "Losa de acacia",             1);
        add(Material.ACACIA_STAIRS,           "Escalera de acacia",         1);
        add(Material.ACACIA_DOOR,             "Puerta de acacia",           1);
        add(Material.ACACIA_TRAPDOOR,         "Trampilla de acacia",        1);
        add(Material.ACACIA_BUTTON,           "Botón de acacia",            1);
        add(Material.ACACIA_PRESSURE_PLATE,   "Placa de acacia",            1);
        // Roble Oscuro
        add(Material.DARK_OAK_SLAB,           "Losa de roble oscuro",       1);
        add(Material.DARK_OAK_STAIRS,         "Escalera de roble oscuro",   1);
        add(Material.DARK_OAK_DOOR,           "Puerta de roble oscuro",     1);
        add(Material.DARK_OAK_TRAPDOOR,       "Trampilla de roble oscuro",  1);
        add(Material.DARK_OAK_BUTTON,         "Botón de roble oscuro",      1);
        add(Material.DARK_OAK_PRESSURE_PLATE, "Placa de roble oscuro",      1);

        // ── DIFICULTAD 2 ──────────────────────────────────
        add(Material.HONEY_BOTTLE,            "Miel embotellada",           2);
        add(Material.GOLDEN_DANDELION,        "Diente de León dorado",      2);
        add(Material.HONEY_BLOCK,             "Bloque de miel",             2);
        add(Material.REDSTONE,                "Redstone",                   2);
        add(Material.LAPIS_LAZULI,            "Lapislázuli",                2);
        add(Material.GLASS,                   "Cristal",                    2);
        add(Material.BRICK,                   "Ladrillo",                   2);
        add(Material.STRING,                  "Hilo",                       2);
        add(Material.GUNPOWDER,               "Pólvora",                    2);
        add(Material.PAPER,                   "Papel",                      2);
        add(Material.BOOK,                    "Libro",                      2);
        add(Material.SLIME_BALL,              "Bola de slime",              2);
        add(Material.PRISMARINE_SHARD,        "Fragmento de prismarina",    2);
        add(Material.GLOWSTONE_DUST,          "Polvo de piedra luminosa",   2);
        add(Material.GLOWSTONE,               "Piedra luminosa",            2);
        add(Material.SPIDER_EYE,              "Ojo de araña",               2);
        add(Material.FERMENTED_SPIDER_EYE,    "Ojo de araña fermentado",    2);
        add(Material.BOWL,                    "Cuenco",                     2);
        add(Material.CLAY_BALL,               "Bola de arcilla",            2);
        add(Material.CLAY,                    "Arcilla",                    2);
        add(Material.FLINT_AND_STEEL,         "Pedernal y acero",           2);
        add(Material.COMPASS,                 "Brújula",                    2);
        add(Material.TORCH,                   "Antorcha",                   2);
        add(Material.LANTERN,                 "Linterna",                   2);
        add(Material.COOKED_COD,              "Bacalao asado",              2);
        add(Material.COOKED_SALMON,           "Salmón asado",               2);
        add(Material.RABBIT_FOOT,             "Pata de conejo",             2);
        add(Material.PUFFERFISH,              "Pez globo",                  2);
        // Manglar
        add(Material.MANGROVE_SLAB,           "Losa de manglar",            2);
        add(Material.MANGROVE_STAIRS,         "Escalera de manglar",        2);
        add(Material.MANGROVE_DOOR,           "Puerta de manglar",          2);
        add(Material.MANGROVE_TRAPDOOR,       "Trampilla de manglar",       2);
        add(Material.MANGROVE_BUTTON,         "Botón de manglar",           2);
        add(Material.MANGROVE_PRESSURE_PLATE, "Placa de manglar",           2);
        // Bambú
        add(Material.BAMBOO_SLAB,             "Losa de bambú",              2);
        add(Material.BAMBOO_STAIRS,           "Escalera de bambú",          2);
        add(Material.BAMBOO_DOOR,             "Puerta de bambú",            2);
        add(Material.BAMBOO_TRAPDOOR,         "Trampilla de bambú",         2);
        add(Material.BAMBOO_BUTTON,           "Botón de bambú",             2);
        add(Material.BAMBOO_PRESSURE_PLATE,   "Placa de bambú",             2);
        // Carmesí
        add(Material.CRIMSON_SLAB,            "Losa carmesí",               2);
        add(Material.CRIMSON_STAIRS,          "Escalera carmesí",           2);
        add(Material.CRIMSON_DOOR,            "Puerta carmesí",             2);
        add(Material.CRIMSON_TRAPDOOR,        "Trampilla carmesí",          2);
        add(Material.CRIMSON_BUTTON,          "Botón carmesí",              2);
        add(Material.CRIMSON_PRESSURE_PLATE,  "Placa carmesí",              2);
        // Distorsionado
        add(Material.WARPED_SLAB,             "Losa distorsionada",         2);
        add(Material.WARPED_STAIRS,           "Escalera distorsionada",     2);
        add(Material.WARPED_DOOR,             "Puerta distorsionada",       2);
        add(Material.WARPED_TRAPDOOR,         "Trampilla distorsionada",    2);
        add(Material.WARPED_BUTTON,           "Botón distorsionado",        2);
        add(Material.WARPED_PRESSURE_PLATE,   "Placa distorsionada",        2);

        // ── DIFICULTAD 3 ──────────────────────────────────
        add(Material.DIAMOND,                 "Diamante",                   3);
        add(Material.EMERALD,                 "Esmeralda",                  3);
        add(Material.IRON_INGOT,              "Lingote de hierro",          3);
        add(Material.GOLD_INGOT,              "Lingote de oro",             3);
        add(Material.ENDER_PEARL,             "Perla de Ender",             3);
        add(Material.OBSIDIAN,                "Obsidiana",                  3);
        add(Material.CLOCK,                   "Reloj",                      3);
        add(Material.MILK_BUCKET,             "Cubo de leche",              3);
        add(Material.LAVA_BUCKET,             "Cubo de lava",               3);
        add(Material.SADDLE,                  "Silla de montar",            3);
        add(Material.NAME_TAG,                "Etiqueta",                   3);
        add(Material.QUARTZ,                  "Cuarzo del Nether",          3, true);
        add(Material.NETHER_WART,             "Verruga del Nether",         3, true);
        add(Material.IRON_SWORD,              "Espada de hierro",           3);
        add(Material.IRON_PICKAXE,            "Pico de hierro",             3);
        add(Material.IRON_AXE,               "Hacha de hierro",            3);
        add(Material.IRON_SHOVEL,             "Pala de hierro",             3);
        add(Material.BUCKET,                  "Cubo vacío",                 3);
        add(Material.WATER_BUCKET,            "Cubo de agua",               3);
        add(Material.PUMPKIN_SEEDS,           "Semillas de calabaza",       3);
        add(Material.MELON_SEEDS,             "Semillas de melón",          3);
        add(Material.GOLDEN_CARROT,           "Zanahoria dorada",           3);
        add(Material.GLISTERING_MELON_SLICE,  "Rodaja de melón brillante",  3);
        add(Material.MAGMA_CREAM,             "Crema de magma",             3, true);
        add(Material.FIRE_CHARGE,             "Bola de fuego",              3, true);
        add(Material.LEAD,                    "Cuerda",                     3);
        // Cerezo
        add(Material.CHERRY_SLAB,             "Losa de cerezo",             3);
        add(Material.CHERRY_STAIRS,           "Escalera de cerezo",         3);
        add(Material.CHERRY_DOOR,             "Puerta de cerezo",           3);
        add(Material.CHERRY_TRAPDOOR,         "Trampilla de cerezo",        3);
        add(Material.CHERRY_BUTTON,           "Botón de cerezo",            3);
        add(Material.CHERRY_PRESSURE_PLATE,   "Placa de cerezo",            3);

        // ── DIFICULTAD 4 ──────────────────────────────────
        add(Material.BLAZE_ROD,               "Vara de Blaze",              4, true);
        add(Material.GHAST_TEAR,              "Lágrima de Ghast",           4, true);
        add(Material.IRON_BLOCK,              "Bloque de hierro",           4);
        add(Material.GOLD_BLOCK,              "Bloque de oro",              4);
        add(Material.SOUL_SAND,               "Arena de almas",             4, true);
        add(Material.POLISHED_BASALT,         "Basalto pulido",             4, true);
        add(Material.POLISHED_BLACKSTONE,     "Piedra negra pulida",        4, true);
        add(Material.SOUL_LANTERN,            "Linterna del alma",          4, true);
        add(Material.QUARTZ_BLOCK,            "Bloque de cuarzo",           4, true);
        add(Material.DIAMOND_SWORD,           "Espada de diamante",         4);
        add(Material.DIAMOND_PICKAXE,         "Pico de diamante",           4);
        add(Material.DIAMOND_AXE,             "Hacha de diamante",          4);
        add(Material.DIAMOND_CHESTPLATE,      "Pechera de diamante",        4);
        add(Material.DIAMOND_HELMET,          "Casco de diamante",          4);
        add(Material.DIAMOND_LEGGINGS,        "Pantalones de diamante",     4);
        add(Material.DIAMOND_BOOTS,           "Botas de diamante",          4);
        add(Material.TURTLE_SCUTE,            "Escama de tortuga",          4);
        add(Material.HEART_OF_THE_SEA,        "Corazón del mar",            4);

        // ── DIFICULTAD 5 ──────────────────────────────────
        add(Material.ENDER_EYE,               "Ojo de Ender",               5);
        add(Material.TURTLE_HELMET,           "Casco de tortuga",           5);
        add(Material.PRISMARINE,              "Prismarina",                 5);
        add(Material.PRISMARINE_BRICKS,       "Ladrillos de prismarina",    5);
        add(Material.DARK_PRISMARINE,         "Prismarina oscura",          5);
        add(Material.SEA_LANTERN,             "Linterna del mar",           5);
        add(Material.NAUTILUS_SHELL,          "Concha de nautilo",          5);
    }

    private static void add(Material mat, String name, int diff) {
        ALL_ITEMS.add(new BingoItem(mat, name, diff, false));
    }

    private static void add(Material mat, String name, int diff, boolean nether) {
        // Avoid duplicate Nether Star
        ALL_ITEMS.removeIf(i -> i.getMaterial() == mat && i.getDifficulty() == 5 && nether && diff == 5);
        ALL_ITEMS.add(new BingoItem(mat, name, diff, nether));
    }

    public static List<BingoItem> getAll() {
        return new ArrayList<>(ALL_ITEMS);
    }

    public static List<BingoItem> getByDifficulty(int difficulty) {
        List<BingoItem> result = new ArrayList<>();
        for (BingoItem item : ALL_ITEMS) {
            if (item.getDifficulty() == difficulty) {
                result.add(item);
            }
        }
        return result;
    }
}
