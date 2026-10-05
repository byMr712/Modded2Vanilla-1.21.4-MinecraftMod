package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.isolator.VanillaMechanicIsolator;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ClientPlayerInteractionManager.class)
public abstract class ClientPlayerInteractionManagerMixin {

    @ModifyVariable(
        method = "clickCreativeStack",
        at = @At("HEAD"),
        argsOnly = true
    )
    private ItemStack modded2Vanilla$sanitizeCreativeStack(ItemStack stack) {
        return VanillaMechanicIsolator.sanitizeCreativeStack(stack);
    }
}
