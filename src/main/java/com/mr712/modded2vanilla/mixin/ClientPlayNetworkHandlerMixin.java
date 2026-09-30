package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundFromEntityS2CPacket;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public abstract class ClientPlayNetworkHandlerMixin {

    @Inject(method = "onGameJoin", at = @At("TAIL"))
    private void modded2Vanilla$onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (IsolatorState.isMultiplayer()) {
            AdjustmentTracker.printNoticeIfAny();
        }
    }

    @Inject(method = "clearWorld", at = @At("HEAD"))
    private void modded2Vanilla$onClearWorld(CallbackInfo ci) {
        AdjustmentTracker.resetSessionNotice();
    }

    @Inject(method = "onPlaySound", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardPlaySound(PlaySoundS2CPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.getSound() == null || packet.getSound().value() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid PlaySoundS2CPacket from server.");
            ci.cancel();
        }
    }

    @Inject(method = "onPlaySoundFromEntity", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardPlaySoundFromEntity(PlaySoundFromEntityS2CPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.getSound() == null || packet.getSound().value() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid PlaySoundFromEntityS2CPacket from server.");
            ci.cancel();
        }
    }

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$guardParticle(ParticleS2CPacket packet, CallbackInfo ci) {
        if (!IsolatorState.isMultiplayer()) {
            return;
        }
        if (packet == null || packet.getParameters() == null) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed invalid ParticleS2CPacket from server.");
            ci.cancel();
        }
    }
}
