package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.render.SafeRenderHelper;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin<T extends LivingEntity, S extends LivingEntityRenderState, M extends EntityModel<? super S>> {

    @Redirect(
        method = "render(Lnet/minecraft/client/render/entity/state/LivingEntityRenderState;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/entity/feature/FeatureRenderer;render(Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;ILnet/minecraft/client/render/entity/state/EntityRenderState;FF)V"
        )
    )
    private void modded2Vanilla$redirectFeatureRender(
        FeatureRenderer<S, M> instance,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        net.minecraft.client.render.entity.state.EntityRenderState state,
        float limbAngle,
        float limbDistance
    ) {
        if (state instanceof LivingEntityRenderState livingState) {
            SafeRenderHelper.safeRenderFeature(
                instance,
                matrices,
                vertexConsumers,
                light,
                (S) livingState,
                limbAngle,
                limbDistance
            );
        } else {
            instance.render(matrices, vertexConsumers, light, (S) state, limbAngle, limbDistance);
        }
    }
}
