package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLevelParticlesPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import net.minecraft.network.protocol.game.ClientboundOpenScreenPacket;
import net.minecraft.network.protocol.game.ClientboundSoundEntityPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPacketListener.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "handleLogin", at = @At("TAIL"))
    private void modded2Vanilla$onGameJoin(ClientboundLoginPacket packet, CallbackInfo ci) {
        if (IsolatorState.isMultiplayer()) {
            AdjustmentTracker.printNoticeIfAny();
        }
    }

    @Inject(method = "clearLevel", at = @At("HEAD"))
    private void modded2Vanilla$onClearWorld(CallbackInfo ci) {
        AdjustmentTracker.resetSessionNotice();
    }

    @Inject(method = "handleOpenScreen", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardOpenScreen(ClientboundOpenScreenPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if (packet == null || packet.getType() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid OpenScreen packet from server.");
            ci.cancel();
        }
    }

    @Inject(method = "handleSoundEvent", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardPlaySound(ClientboundSoundPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.getSound() == null || packet.getSound().value() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid PlaySound packet from server.");
            ci.cancel();
        }
    }

    @Inject(method = "handleSoundEntityEvent", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardPlaySoundFromEntity(ClientboundSoundEntityPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.getSound() == null || packet.getSound().value() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid PlaySoundEntity packet from server.");
            ci.cancel();
        }
    }

    @Inject(method = "handleParticleEvent", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardParticle(ClientboundLevelParticlesPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.particle() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid Particle packet from server.");
            ci.cancel();
        }
    }
}
