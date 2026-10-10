package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.isolator.MovementIsolator;
import com.mr712.modded2vanilla.state.IsolatorState;
import net.minecraft.block.Blocks;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = LivingEntity.class, priority = 500)
public abstract class LivingEntityMixin {

    @Inject(method = "getStepHeight", at = @At("RETURN"), cancellable = true)
    private void mrModded2Vanilla$clampLivingStepHeight(CallbackInfoReturnable<Float> cir) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        if ((Object) this instanceof ClientPlayerEntity) {
            float current = cir.getReturnValue();
            if (current > MovementIsolator.VANILLA_MAX_STEP_HEIGHT) {
                cir.setReturnValue(MovementIsolator.VANILLA_MAX_STEP_HEIGHT);
                MovementIsolator.detectAndRecordCallerMod();
            }
        }
    }

    @Inject(method = "applyClimbingSpeed", at = @At("HEAD"), cancellable = true)
    private void mrModded2Vanilla$pureVanillaApplyClimbingSpeed(Vec3d motion, CallbackInfoReturnable<Vec3d> cir) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ClientPlayerEntity clientPlayer) {
            if (clientPlayer.isClimbing()) {
                clientPlayer.onLanding();
                double x = MathHelper.clamp(motion.x, -0.15000000596046448D, 0.15000000596046448D);
                double z = MathHelper.clamp(motion.z, -0.15000000596046448D, 0.15000000596046448D);
                double y = Math.max(motion.y, -0.15000000596046448D);
                if (y < 0.0D && !clientPlayer.getBlockStateAtPos().isOf(Blocks.SCAFFOLDING) && clientPlayer.isSneaking()) {
                    y = 0.0D;
                }
                cir.setReturnValue(new Vec3d(x, y, z));
            }
        }
    }

    @Inject(method = "jump", at = @At("HEAD"), cancellable = true)
    private void mrModded2Vanilla$guardJumpInMultiplayer(CallbackInfo ci) {
        if (!IsolatorState.isIsolating()) {
            return;
        }
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity instanceof ClientPlayerEntity clientPlayer) {
            // Vanilla invariant: Cannot jump mid-air without ground, ladder, or fluid contact
            if (!clientPlayer.isOnGround() && !clientPlayer.isClimbing() && !clientPlayer.isTouchingWater() && !clientPlayer.isInLava()) {
                ci.cancel();
                MovementIsolator.detectAndRecordCallerMod();
            }
        }
    }
}


