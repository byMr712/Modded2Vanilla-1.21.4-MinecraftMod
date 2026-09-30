package com.mr712.modded2vanilla.tracker;

import com.mr712.modded2vanilla.Modded2Vanilla;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;

import java.nio.file.Path;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public final class AdjustmentTracker {

    private static final Set<String> AFFECTED_JARS = Collections.synchronizedSet(new LinkedHashSet<>());
    private static final AtomicBoolean NOTICE_PRINTED = new AtomicBoolean(false);

    private AdjustmentTracker() {
    }

    public static void recordMod(String modId) {
        if (modId == null || modId.isEmpty() || "minecraft".equalsIgnoreCase(modId)) {
            return;
        }
        String jarName = resolveJarName(modId);
        AFFECTED_JARS.add(jarName);
    }

    public static void recordClass(Class<?> clazz) {
        if (clazz == null) {
            return;
        }
        String className = clazz.getName();
        if (className.startsWith("net.minecraft.") || className.startsWith("java.") || className.startsWith("com.mr712.modded2vanilla.")) {
            return;
        }
        // Attempt to find mod container by code source or package
        for (ModContainer container : FabricLoader.getInstance().getAllMods()) {
            String modId = container.getMetadata().getId();
            if ("minecraft".equals(modId) || "fabricloader".equals(modId) || "java".equals(modId)) {
                continue;
            }
            if (className.toLowerCase().contains(modId.toLowerCase().replace("-", "").replace("_", ""))) {
                recordMod(modId);
                return;
            }
        }
        recordMod(className);
    }

    public static String resolveJarName(String modId) {
        Optional<ModContainer> containerOpt = FabricLoader.getInstance().getModContainer(modId);
        if (containerOpt.isPresent()) {
            ModContainer container = containerOpt.get();
            try {
                List<Path> paths = container.getOrigin().getPaths();
                if (paths != null && !paths.isEmpty()) {
                    String fileName = paths.get(0).getFileName().toString();
                    if (fileName.endsWith(".jar")) {
                        return fileName;
                    }
                }
            } catch (Throwable ignored) {
            }
            return container.getMetadata().getId() + "-" + container.getMetadata().getVersion().getFriendlyString() + ".jar";
        }
        return modId.endsWith(".jar") ? modId : (modId + ".jar");
    }

    public static void printNoticeIfAny() {
        if (AFFECTED_JARS.isEmpty()) {
            return;
        }
        if (!NOTICE_PRINTED.compareAndSet(false, true)) {
            return;
        }

        StringBuilder builder = new StringBuilder();
        builder.append("\n\n");
        builder.append("==========[Modded2Vanilla]=============\n");
        builder.append("Notice: Modded2Vanilla detected mods incompatible with multiplayer/vanilla servers and applied runtime adjustments. \n");
        builder.append("This will not affect your singleplayer experience, enjoy your game!\n\n");
        builder.append("Affected mods: \n");
        synchronized (AFFECTED_JARS) {
            for (String jar : AFFECTED_JARS) {
                builder.append("[").append(jar).append("]\n");
            }
        }
        builder.append("==========[Modded2Vanilla]=============\n\n");

        Modded2Vanilla.LOGGER.info("{}", builder);
    }

    public static void resetSessionNotice() {
        NOTICE_PRINTED.set(false);
    }

    public static boolean hasAdjustments() {
        return !AFFECTED_JARS.isEmpty();
    }
}
