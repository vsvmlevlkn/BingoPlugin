# 🎯 BingoPlugin — Bingo por Equipos 5×5 para Minecraft

Plugin competitivo de Bingo para Paper 1.20-1.21.

## 📦 Compilación

Requisitos: **JDK 17+** y **Maven 3.8+**

```bash
cd BingoPlugin
mvn clean package
```

El JAR compilado se generará en `target/BingoPlugin-1.0.0.jar`.

## 🚀 Instalación

1. Copia `BingoPlugin-1.0.0.jar` a la carpeta `plugins/` de tu servidor Paper.
2. Reinicia el servidor.
3. El archivo `config.yml` se generará automáticamente en `plugins/BingoPlugin/`.

## 🎮 Comandos

### Administradores (op)
| Comando | Descripción |
|---------|-------------|
| `/bingo start` | Inicia la partida y genera cartones |
| `/bingo stop` | Detiene la partida |
| `/bingo reload` | Recarga la configuración |
| `/bingo reroll` | Genera nuevos cartones |

### Jugadores
| Comando | Descripción |
|---------|-------------|
| `/bingo team <nombre>` | Unirse a un equipo |
| `/bingo board` | Ver el cartón en el chat |
| `/bingo map` | Recibir el mapa del cartón |

### Equipos disponibles
`Rojo`, `Azul`, `Verde`, `Amarillo`, `Morado`, `Naranja`

## 🔧 Configuración (`config.yml`)

```yaml
max-point-difference: 5    # Diferencia máxima de puntos entre cartones
nether-items-min: 3        # Mínimo de items del Nether por cartón
nether-items-max: 5        # Máximo de items del Nether por cartón
team-size: 2               # Jugadores por equipo
```

## 📋 Reglas del juego

- Cada equipo recibe un cartón 5×5 único.
- Un objeto se considera conseguido si **cualquier miembro** del equipo lo tiene en su inventario.
- Si el objeto sale del inventario de todos los miembros, **deja de contar**.
- Se gana completando una **fila**, **columna** o **diagonal**.
- El cartón se muestra como **mapa personalizado** que se actualiza en tiempo real.

## 🚫 Objetos prohibidos

- Items del End (Elytra, Chorus, Shulker, etc.)
- Netherita y sus derivados
- Items encantados
- Menas que requieren Toque de Seda

## ⚖️ Sistema de dificultad

| Puntos | Nivel | Ejemplos |
|--------|-------|---------|
| 1 ★ | Muy fácil | Pan, Huevo, Pluma, Carbón |
| 2 ★★ | Fácil | Redstone, Pólvora, Bola de Slime |
| 3 ★★★ | Media | Diamante, Perla de Ender, Obsidiana |
| 4 ★★★★ | Difícil | Vara de Blaze, Lágrima de Ghast |
| 5 ★★★★★ | Muy difícil | Beacon, Estrella del Nether, Bloque de Diamante |

Cada cartón tiene ~60 puntos. La diferencia máxima entre equipos es de 5 puntos.

## 🗂️ Estructura del proyecto

```
BingoPlugin/
├── pom.xml
└── src/main/java/com/bingo/
    ├── BingoPlugin.java          # Main class
    ├── commands/
    │   └── BingoCommand.java     # Todos los /bingo subcommands
    ├── listeners/
    │   ├── InventoryListener.java # Tracking de inventario
    │   └── PlayerListener.java    # Join/quit handling
    ├── managers/
    │   ├── GameManager.java      # Ciclo de vida del juego
    │   └── TeamManager.java      # Gestión de equipos
    ├── map/
    │   ├── BingoMapRenderer.java # Renderizado del cartón en mapa
    │   └── MapManager.java       # Distribución de mapas
    ├── models/
    │   ├── BingoCard.java        # Cartón 5×5 y condiciones de victoria
    │   ├── BingoItem.java        # Item con dificultad
    │   └── BingoTeam.java        # Equipo y sus miembros
    └── utils/
        ├── CardGenerator.java    # Generación balanceada de cartones
        └── ItemPool.java         # Pool de ~70 items válidos clasificados
```
