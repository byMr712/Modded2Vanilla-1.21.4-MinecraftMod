package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.snapshot.VanillaBlockStateSnapshot;
import com.mr712.modded2vanilla.state.IsolatorState;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Block.class)
public abstract class BlockMixin {

    @Inject(method = "getStateFromRawId", at = @At("HEAD"), cancellable = true)
    private static void onGetStateFromRawId(int id, CallbackInfoReturnable<BlockState> cir) {
        if (IsolatorState.isIsolating()) {
            BlockState canonical = VanillaBlockStateSnapshot.resolveState(id);
            if (canonical != null) {
                cir.setReturnValue(canonical);
            }
        }
    }
}
