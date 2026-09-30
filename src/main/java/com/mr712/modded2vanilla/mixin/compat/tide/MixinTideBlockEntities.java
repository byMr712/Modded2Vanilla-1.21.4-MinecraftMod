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
@Mixin(targets = "com.li64.tide.registries.TideBlockEntities", remap = false)
public abstract class MixinTideBlockEntities {

    @Redirect(
        method = "register(Ljava/lang/String;Lnet/minecraft/class_2591;)Lnet/minecraft/class_2591;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/class_2378;method_10226(Lnet/minecraft/class_2378;Ljava/lang/String;Ljava/lang/Object;)Ljava/lang/Object;"
        ),
        remap = false,
        require = 0
    )
    private static Object deferRegisterBlockEntity(Registry<?> registry, String name, Object blockEntityType) {
        return TideRegistryDeferHandler.deferBlockEntity(name, blockEntityType);
    }

    @Inject(method = "init", at = @At("HEAD"), remap = false, require = 0)
    private static void onInit(CallbackInfo ci) {
        TideRegistryDeferHandler.flush();
    }
}
