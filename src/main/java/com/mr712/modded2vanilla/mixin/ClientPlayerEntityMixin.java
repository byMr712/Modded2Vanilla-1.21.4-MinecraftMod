package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.isolator.MovementIsolator;
import com.mr712.modded2vanilla.state.IsolatorState;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(method = "sendMovementPackets", at = @At("HEAD"))
    private void modded2Vanilla$onSendMovementPackets(CallbackInfo ci) {
        if (IsolatorState.isIsolating()) {
            MovementIsolator.detectAndRecordCallerMod();
        }
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"))
    private void modded2Vanilla$onPushOutOfBlocks(double x, double d, CallbackInfo ci) {
        if (IsolatorState.isIsolating()) {
            MovementIsolator.detectAndRecordCallerMod();
        }
    }
}
