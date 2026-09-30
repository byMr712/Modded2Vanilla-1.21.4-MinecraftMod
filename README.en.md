> **Language:** [Русский](README.md) · English

# Modded2Vanilla (Minecraft 1.21.4 Fabric)

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue.svg)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue.svg)
![ModMenu](https://img.shields.io/badge/ModMenu-Supported-blue.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

**Modded2Vanilla** is a universal client-side mod for **Minecraft 1.21.4 (Fabric)** that allows you to play with your favorite content mods (blocks, entities, food, weapons, decorations) in singleplayer and seamlessly connect to any external servers (Vanilla, Paper, Purpur, Spigot, Realms, and via ViaFabricPlus) without disabling mods or swapping launcher profiles.

---

## Purpose & Problem Solved

When content mods are installed on the client, they register blocks, items, and entities into Minecraft's global registries. Connecting to standard multiplayer servers (Vanilla, Paper, Spigot) or using **ViaFabricPlus** causes network collisions:
- Global block and item IDs shift (e.g., vanilla sugar cane or logs visually turning into modded blocks).
- Entity metadata packets break (`Invalid entity data item type`), causing crashes upon encountering vanilla mobs.
- Registry mismatches pollute network packet handling.

**Modded2Vanilla** automatically detects connection types:
- When joining vanilla/remote servers, it isolates mod data and provides an exact standard vanilla registry mapping.
- **If a mod is installed on both client and server** — its functionality is unaffected, synchronizing custom packets and data normally.

---

## What Modded2Vanilla Does

1. **Block Palette Protection (`Block.STATE_IDS` / Network Chunks)**:
   - Intercepts lookups to global block state registries and serves a clean 1.21.4 vanilla palette, preventing visual block desyncs in the world.
2. **Item ID Normalization (`Item.byRawId`)**:
   - Ensures strict alignment of network item packets to standard vanilla IDs.
3. **Smart Entity Metadata Protection (`DataTracker`)**:
   - Automatically validates entity data types (`entryIdMatches`).
   - If the server is vanilla and a client mod shifts `LivingEntity` indices, it safely re-aligns them.
   - If the server has the mod and sends custom data, it passes through untouched.
4. **Seamless ViaFabricPlus Support**:
   - When using **ViaFabricPlus**, prevents version translation conflicts (from 1.8 to 1.21.x), ensuring protocol translators work with pristine vanilla ID tables.

---

## Automatic Mode Switching

The mod requires no configuration and works completely automatically:

| Game Mode | Behavior |
|---|---|
| Singleplayer | Isolation disabled: all custom blocks, mobs, recipes, and items work fully |
| Vanilla Server (Vanilla / Paper / Purpur / Spigot / Realms) | Isolation active: seamless connection with zero registry conflicts or crashes |
| Modded Server (Fabric) | Isolation passive: mods present on the server work and sync normally |
| Connection via ViaFabricPlus (any version) | Isolation active: protocol translators use clean reference vanilla tables |

---

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/byMr712/Modded2Vanilla-1.21.4-MinecraftMod/releases).
2. Requires:
   - [Fabric API](https://modrinth.com/mod/fabric-api)
3. Place the `.jar` file into your `mods` folder.
4. Launch the game.

---

## Building

1. Requires Java 21 and Fabric Loader for Minecraft 1.21.4.
2. To build the project, run:
   ```bash
   ./gradlew build
   ```
3. The built jar file will be located at `build/libs/Modded2Vanilla-1.21.4-byMr712.jar`.

---

## Credits & License

- Author: [Mr712](https://github.com/byMr712).
- Distributed under the [Apache License 2.0](LICENSE).