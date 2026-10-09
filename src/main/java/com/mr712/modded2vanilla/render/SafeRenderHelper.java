package com.mr712.modded2vanilla.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public final class SafeRenderHelper {

    private static final Set<String> LOGGED_CRASHING_RENDERERS = Collections.synchronizedSet(new HashSet<>());

    private SafeRenderHelper() {
    }

    @SuppressWarnings("unchecked")
    public static void safeRenderFeature(
        RenderLayer<?, ?> layer,
        PoseStack poseStack,
        SubmitNodeCollector submitNodeCollector,
        int light,
        EntityRenderState state,
        float limbAngle,
        float limbDistance
    ) {
        if (layer == null) {
            return;
        }

        if (!IsolatorState.isIsolating()) {
            ((RenderLayer<EntityRenderState, ?>) layer).submit(poseStack, submitNodeCollector, light, state, limbAngle, limbDistance);
            return;
        }

        try {
            ((RenderLayer<EntityRenderState, ?>) layer).submit(poseStack, submitNodeCollector, light, state, limbAngle, limbDistance);
        } catch (Throwable t) {
            String className = layer.getClass().getName();
            if (LOGGED_CRASHING_RENDERERS.add(className)) {
                Modded2Vanilla.LOGGER.warn("[Modded2Vanilla] Handled crash in RenderLayer {}: {}", className, t.getMessage());
                AdjustmentTracker.recordClassName(className);
            }
        }
    }
}
