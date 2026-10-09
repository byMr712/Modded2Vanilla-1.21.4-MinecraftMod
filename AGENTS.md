# Developer & Agent Guidelines — MrModded2Vanilla (1.21)

## 1. Project Overview & Identity
- **Mod Name:** MrModded2Vanilla
- **Display Name in Mod Menu:** `[MR] Modded2Vanilla`
- **Target Minecraft Version:** 1.21
- **Loader:** Fabric Loader (`0.19.5+` / `>=0.16.0`)
- **Mapping Stack:** Yarn `1.21+build.9:v2`
- **Fabric API:** `0.102.0+1.21`
- **Java Requirement:** Java 21 LTS
- **Build Output:** `MrModded2Vanilla-Fabric-1.21-byMr712-v1.0.jar`
- **Repository:** https://github.com/byMr712/Modded2Vanilla-MinecraftMod
- **Author:** [Mr712](https://github.com/byMr712)
- **License:** Apache-2.0

---

## 2. Core Architecture & Modules

### 2.1 Universal Registry Buffer (`UniversalRegistryBuffer` + `RegistryMixin`)
- Intercepts registration attempts during `Bootstrap.initialize()` when namespace != `"minecraft"`.
- Modded entries are temporarily held in `UniversalRegistryBuffer` and flushed cleanly at `Bootstrap.initialize() -> TAIL`.
- Ensures canonical vanilla block/item states occupy deterministic initial palette indices `0..N`.

### 2.2 Canonical Block State Snapshot (`VanillaBlockStateSnapshot` + `BlockMixin`)
- Captures an immutable snapshot of pure vanilla block states right after bootstrap completion.
- When isolating in multiplayer (`IsolatorState.isIsolating()`), `Block.getStateFromRawId(id)` resolves against the snapshot, preventing ID shifts and mismatch.

### 2.3 Entity Metadata Compensation (`DataTrackerMixin`)
- Resolves discrepancies in entity metadata entries (`DataTracker`) between client and server.
- Automatically handles index shifts (`+1, -1, +2, -2...`) and matches handlers safely, preventing `Invalid entity data item type` crashes.

### 2.4 Mechanics & Interaction Isolation (`VanillaMechanicIsolator`, `LivingEntityMixin`, `PlayerEntityMixin`)
- Clamps player step height to vanilla `0.6F`.
- Normalizes climbing speeds on ladders, vines, and scaffolding.
- Restricts mid-air jumps without physical support.
- Enforces strict vanilla reach distance (survival: 4.5 blocks, creative: 5.0 blocks, entity reach: 3.0 blocks).
- Sanitizes creative item components (`ComponentSanitizer`) before dispatch to vanilla servers.

### 2.5 Safe Feature Rendering (`SafeRenderHelper` + `LivingEntityRendererMixin`)
- Wraps `FeatureRenderer.render()` in try-catch handlers to prevent client crashes from malformed third-party cosmetics or animations.

### 2.6 Adjustment Tracker (`AdjustmentTracker`)
- Detects and records any non-system classes and third-party mod JARs that triggered runtime isolation.
- Emits exactly one formatted notice to the client log upon connecting to a multiplayer server.

---

## 3. Version Nuances (Minecraft 1.21)
- **Entity Rendering Pipeline:** Minecraft 1.21 renders entities directly via `LivingEntityRenderer.render(T, float, float, MatrixStack, VertexConsumerProvider, int)` without `LivingEntityRenderState` (which was introduced in 1.21.2).
- **Feature Rendering:** `FeatureRenderer.render()` accepts individual pose and animation float parameters `(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch)`.
- **Network Dispatch:** Uses standard Netty `PacketCallbacks` on `ClientConnection.send`.
- **DataTracker Architecture:** `DataTracker` uses array-based storage `DataTracker.Entry<?>[] entries` and `DataTracked` interface.

---

## 4. Build Instructions
- Prerequisites: Java 21 or Java 25 JDK installed.
- Compile and build:
  ```bash
  ./gradlew build
  ```
- Resulting jar: `build/libs/MrModded2Vanilla-Fabric-1.21-byMr712-v1.0.jar`.