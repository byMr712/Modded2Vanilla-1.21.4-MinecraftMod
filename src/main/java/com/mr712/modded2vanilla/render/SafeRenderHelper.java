package com.mr712.modded2vanilla.render;

import com.mr712.modded2vanilla.MrModded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SafeRenderHelper {

    private static final Set<String> LOGGED_CRASHING_RENDERERS = Collections.synchronizedSet(new HashSet<>());

    private SafeRenderHelper() {
    }

    public static <T extends LivingEntity> void safeRenderFeature(
        FeatureRenderer<T, ?> featureRenderer,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        T entity,
        float limbAngle,
        float limbDistance,
        float tickDelta,
        float animationProgress,
        float headYaw,
        float headPitch
    ) {
        if (featureRenderer == null) {
            return;
        }

        if (!IsolatorState.isIsolating()) {
            featureRenderer.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
            return;
        }

        try {
            featureRenderer.render(matrices, vertexConsumers, light, entity, limbAngle, limbDistance, tickDelta, animationProgress, headYaw, headPitch);
        } catch (Throwable t) {
            String className = featureRenderer.getClass().getName();
            if (LOGGED_CRASHING_RENDERERS.add(className)) {
                MrModded2Vanilla.LOGGER.warn("[MrModded2Vanilla] Handled crash in FeatureRenderer {}: {}", className, t.getMessage());
                AdjustmentTracker.recordClassName(className);
            }
        }
    }
}
