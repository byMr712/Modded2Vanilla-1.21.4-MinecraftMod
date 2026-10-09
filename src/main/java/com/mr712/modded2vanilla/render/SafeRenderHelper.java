package com.mr712.modded2vanilla.render;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.state.LivingEntityRenderState;
import net.minecraft.client.util.math.MatrixStack;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SafeRenderHelper {

    private static final Set<String> LOGGED_CRASHING_RENDERERS = Collections.synchronizedSet(new HashSet<>());

    private SafeRenderHelper() {
    }

    public static <S extends LivingEntityRenderState> void safeRenderFeature(
        FeatureRenderer<S, ?> featureRenderer,
        MatrixStack matrices,
        OrderedRenderCommandQueue renderQueue,
        int light,
        S state,
        float limbAngle,
        float limbDistance
    ) {
        if (featureRenderer == null) {
            return;
        }

        if (!IsolatorState.isIsolating()) {
            featureRenderer.render(matrices, renderQueue, light, state, limbAngle, limbDistance);
            return;
        }

        try {
            featureRenderer.render(matrices, renderQueue, light, state, limbAngle, limbDistance);
        } catch (Throwable t) {
            String className = featureRenderer.getClass().getName();
            if (LOGGED_CRASHING_RENDERERS.add(className)) {
                Modded2Vanilla.LOGGER.warn("[Modded2Vanilla] Handled crash in FeatureRenderer {}: {}", className, t.getMessage());
                AdjustmentTracker.recordClassName(className);
            }
        }
    }
}
