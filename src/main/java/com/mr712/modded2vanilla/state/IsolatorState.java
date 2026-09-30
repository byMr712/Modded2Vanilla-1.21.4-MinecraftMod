package com.mr712.modded2vanilla.state;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ServerInfo;

public final class IsolatorState {
    private static volatile boolean forceEnabled = false;
    private static volatile boolean forceDisabled = false;

    private IsolatorState() {
    }

    public static boolean isIsolating() {
        if (forceDisabled) {
            return false;
        }
        if (forceEnabled) {
            return true;
        }
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) {
            return false;
        }
        // In singleplayer, all mods and modded blocks remain completely functional
        if (client.isInSingleplayer()) {
            return false;
        }
        ClientPlayNetworkHandler networkHandler = client.getNetworkHandler();
        if (networkHandler == null) {
            return false;
        }
        // In multiplayer, isolate vanilla registries and entity metadata from mod pollution
        return true;
    }

    public static boolean isCompensatingDataTracker() {
        return isIsolating();
    }

    public static boolean isSingleplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && client.isInSingleplayer();
    }

    public static boolean isMultiplayer() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null && !client.isInSingleplayer() && client.getNetworkHandler() != null;
    }

    public static String getServerBrand() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.getNetworkHandler() == null) {
            return "unknown";
        }
        String brand = client.getNetworkHandler().getBrand();
        return brand != null ? brand : "unknown";
    }

    public static ServerInfo getCurrentServerEntry() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client != null ? client.getCurrentServerEntry() : null;
    }

    public static void setForceEnabled(boolean enabled) {
        forceEnabled = enabled;
    }

    public static void setForceDisabled(boolean disabled) {
        forceDisabled = disabled;
    }
}
