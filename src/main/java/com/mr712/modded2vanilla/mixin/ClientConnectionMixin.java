package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import io.netty.channel.ChannelFutureListener;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.common.CustomPayloadC2SPacket;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ClientConnection.class, priority = 2000)
public abstract class ClientConnectionMixin {

    @Inject(
        method = "send(Lnet/minecraft/network/packet/Packet;Lio/netty/channel/ChannelFutureListener;Z)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void modded2Vanilla$filterOutgoingCustomPayload(Packet<?> packet, ChannelFutureListener callbacks, boolean flush, CallbackInfo ci) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if (packet instanceof CustomPayloadC2SPacket customPacket) {
            CustomPayload payload = customPacket.payload();
            if (payload != null && payload.getId() != null) {
                Identifier channelId = payload.getId().id();
                if (channelId != null && !IsolatorState.isChannelSupportedByServer(channelId)) {
                    Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Suppressed unsupported outgoing CustomPayload channel: {}", channelId);
                    AdjustmentTracker.recordMod(channelId.getNamespace());
                    ci.cancel();
                }
            }
        }
    }
}
