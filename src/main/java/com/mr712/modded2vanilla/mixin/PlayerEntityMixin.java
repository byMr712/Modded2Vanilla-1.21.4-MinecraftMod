package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.isolator.InteractionIsolator;
import com.mr712.modded2vanilla.isolator.MovementIsolator;
import com.mr712.modded2vanilla.state.IsolatorState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerEntity.class, priority = 2000)
public abstract class PlayerEntityMixin {

    @Inject(method = "clipAtLedge", at = @At("RETURN"), cancellable = true)
    private void mrModded2Vanilla$guardClipAtLedge(CallbackInfoReturnable<Boolean> cir) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if ((Object) this instanceof ClientPlayerEntity clientPlayer) {
            boolean isSneaking = clientPlayer.isSneaking();
            boolean returned = cir.getReturnValue();
            // In vanilla, clipAtLedge strictly equals isSneaking()
            if (returned != isSneaking) {
                cir.setReturnValue(isSneaking);
                MovementIsolator.detectAndRecordCallerMod();
            }
        }
    }

    @Inject(method = "getBlockInteractionRange", at = @At("RETURN"), cancellable = true)
    private void mrModded2Vanilla$clampBlockInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if ((Object) this instanceof ClientPlayerEntity clientPlayer) {
            double current = cir.getReturnValue();
            cir.setReturnValue(InteractionIsolator.sanitizeBlockInteractionRange(clientPlayer, current));
        }
    }

    @Inject(method = "getEntityInteractionRange", at = @At("RETURN"), cancellable = true)
    private void mrModded2Vanilla$clampEntityInteractionRange(CallbackInfoReturnable<Double> cir) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if ((Object) this instanceof ClientPlayerEntity) {
            double current = cir.getReturnValue();
            cir.setReturnValue(InteractionIsolator.sanitizeEntityInteractionRange(current));
        }
    }
}
