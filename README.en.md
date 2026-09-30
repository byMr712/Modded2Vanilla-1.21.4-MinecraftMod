> **Language:** [Русский](README.md) · English

# Modded2Vanilla (Minecraft 1.21.4 Fabric)

![Java 21](https://img.shields.io/badge/Java-21-blue.svg)
![Minecraft](https://img.shields.io/badge/Minecraft-1.21.4-blue.svg)
![Fabric](https://img.shields.io/badge/Loader-Fabric-blue.svg)
![License](https://img.shields.io/badge/License-Apache_2.0-blue.svg)

Client-side mod for **Minecraft 1.21.4 (Fabric)** that ensures seamless compatibility between modded client setups and vanilla/multiplayer servers.

---

## About

**Modded2Vanilla** is a specialized client-side isolation and compatibility layer. It allows players to enjoy content mods (adding custom blocks, entities, weapons, armor, food, decorations, and items) in singleplayer while seamlessly connecting to any external multiplayer servers (Vanilla, Paper, Purpur, Spigot, Realms, and via ViaFabricPlus) without disabling mods or maintaining separate launcher profiles.

---

## Problem the Mod Solves

When content mods are installed on a client, they expand entity metadata tables (`DataTracker`) and register custom registry entries. Connecting with such a client to standard servers (Vanilla, Paper, Spigot) or across versions via **ViaFabricPlus** causes network desynchronization:
- Entity metadata index collisions occur (`DataTracker`), causing `Invalid entity data item type` and `ArrayIndexOutOfBoundsException` crashes when spawning mobs or players.
- Network packet handling issues arise from unmapped sound, particle, or screen identifiers.
- Early mod initialization can interfere with standard registry ordering.

---

## Features

- **Smart Entity Metadata Protection (`DataTracker`)**:
  - Automatically validates entity data handler types between server and client.
  - Dynamically re-aligns shifted indices when connecting to vanilla servers.
  - Prevents crashes and sudden client disconnects from incompatible entity packets.
- **Safe Entity Render Layer**:
  - Guards against client crashes caused by failing third-party `FeatureRenderer` implementations (custom animations, 3D armor models, cosmetics), rendering fallback base entity models cleanly.
- **Intelligent Outgoing Packet Filtering (`CustomPayload`)**:
  - Automatically suppresses unannounced modded payload channels on strict vanilla servers to prevent anti-cheat kicks (*"Unknown custom packet channel"*), while preserving full communication on modded servers that declare support.
- **Conventional / Fabric Tags Client Fallback**:
  - Automatically binds conventional tags (`c:ores`, `c:chests`, `c:tools`, `c:shears`) to vanilla equivalents on servers that omit non-minecraft tags, ensuring inventory sorters, HUDs, maps, and tool mods function properly.
- **Data Component Sanitizer (`Data Components`)**:
  - Sanitizes inventory interaction packets destined for vanilla servers by stripping mod-specific component keys, preventing server kicks due to unrecognized item stack components.
- **Safe Network Packet Handling**:
  - Filters malformed or unmapped sound, particle, and screen packets (`OpenScreenS2CPacket`), preventing `Internal Exception` disconnects.
- **Early Registry Initialization Isolation & Canonical State Snapshots**:
  - Protects vanilla registries from pollution and guarantees a canonical 1:1 block palette on multiplayer servers.
- **Seamless ViaFabricPlus Support**:
  - Maintains clean metadata environments for network protocol translators when connecting to legacy server versions (1.8 through 1.21.x).
- **Comprehensive Adjustment Notifications**:
  - Logs a formatted notice banner listing all affected `.jar` files upon client startup and server join when adjustments are applied.

---

## Automatic Mode Distribution

The mod requires no manual setup and adjusts its behavior automatically:

| Game Mode | Behavior |
|---|---|
| Singleplayer | Isolation disabled: all custom blocks, mobs, recipes, and modded items work in full |
| Vanilla Server (Vanilla / Paper / Purpur / Spigot / Realms) | Isolation active: connect seamlessly with no registry conflicts or DataTracker crashes |
| Modded Server (Fabric) | Isolation passive: mods present on both client and server synchronize normally |
| Connection via ViaFabricPlus (any version) | Isolation active: protocol translators function reliably with clean metadata |

---

## Installation

1. Download the latest release from [GitHub Releases](https://github.com/byMr712/Modded2Vanilla-1.21.4-MinecraftMod/releases).
2. Requires [Fabric API](https://modrinth.com/mod/fabric-api).
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
- Source Code: [GitHub](https://github.com/byMr712/Modded2Vanilla-1.21.4-MinecraftMod).
- Distributed under the [Apache License 2.0](LICENSE).