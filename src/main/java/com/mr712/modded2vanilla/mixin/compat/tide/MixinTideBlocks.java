package com.mr712.modded2vanilla.mixin.compat.tide;

import com.mr712.modded2vanilla.compat.tide.TideRegistryDeferHandler;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@SuppressWarnings({"UnresolvedMixinReference", "MixinAnnotationTarget"})
@Pseudo
@Mixin(targets = "com.li64.tide.registries.TideBlocks", remap = false)
public abstract class MixinTideBlocks {

    @Redirect(
        method = "register(Ljava/lang/String;Ljava/util/function/Function;Lnet/minecraft/class_4970$class_2251;)Lnet/minecraft/class_2248;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_2378;method_39197(Lnet/minecraft/class_2378;Lnet/minecraft/class_5321;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 0
    )
    private static Object deferRegisterBlock(Registry<?> registry, RegistryKey<?> key, Object block) {
        return TideRegistryDeferHandler.deferBlock(key, block);
    }

    @Inject(method = "init", at = @At("HEAD"), remap = false, require = 0)
    private static void onInit(CallbackInfo ci) {
        TideRegistryDeferHandler.flush();
    }
}
