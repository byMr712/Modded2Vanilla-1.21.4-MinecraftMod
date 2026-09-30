package com.mr712.modded2vanilla.mixin;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.data.DataTracked;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Objects;

@Mixin(DataTracker.class)
public abstract class DataTrackerMixin {
    @Shadow
    private DataTracked trackedEntity;

    @Shadow
    private DataTracker.Entry<?>[] entries;

    @Inject(method = "registerData", at = @At("HEAD"))
    private static <T> void modded2Vanilla$onRegisterData(
        Class<? extends Entity> entityClass, TrackedDataHandler<T> dataHandler, CallbackInfoReturnable<TrackedData<T>> cir
    ) {
        if (entityClass != null && entityClass.getName().startsWith("net.minecraft.")) {
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();
            for (StackTraceElement element : stack) {
                String className = element.getClassName();
                if (!className.startsWith("net.minecraft.") &&
                    !className.startsWith("java.") &&
                    !className.startsWith("jdk.") &&
                    !className.startsWith("org.spongepowered.") &&
                    !className.startsWith("com.mr712.modded2vanilla.")) {
                    AdjustmentTracker.recordClassName(className);
                    break;
                }
            }
        }
    }

    @Inject(method = "writeUpdatedEntries", at = @At("HEAD"), cancellable = true)
    private void modded2Vanilla$safeWriteUpdatedEntries(
        List<DataTracker.SerializedEntry<?>> list, CallbackInfo ci
    ) {
        if (list == null || list.isEmpty()) {
            ci.cancel();
            return;
        }

        // In singleplayer, preserve normal vanilla and modded execution
        if (!IsolatorState.isCompensatingDataTracker()) {
            return;
        }

        ci.cancel();

        if (this.entries == null || this.entries.length == 0) {
            return;
        }

        for (final DataTracker.SerializedEntry<?> serializedEntry : list) {
            if (serializedEntry == null) {
                continue;
            }

            final int rawId = serializedEntry.id();
            final DataTracker.Entry<?> targetEntry = resolveMatchingEntry(rawId, serializedEntry);

            if (targetEntry != null) {
                if (targetEntry.getData() != null && targetEntry.getData().id() != rawId && this.trackedEntity != null) {
                    AdjustmentTracker.recordClass(this.trackedEntity.getClass());
                }
                try {
                    copySafely(targetEntry, serializedEntry);
                    if (this.trackedEntity != null) {
                        this.trackedEntity.onTrackedDataSet(targetEntry.getData());
                    }
                } catch (Throwable t) {
                    Modded2Vanilla.LOGGER.debug(
                        "[Modded2Vanilla] Handled data update exception for entity {}: {}",
                        this.trackedEntity, t.getMessage()
                    );
                }
            } else {
                if (this.trackedEntity != null) {
                    AdjustmentTracker.recordClass(this.trackedEntity.getClass());
                }
                Modded2Vanilla.LOGGER.debug(
                    "[Modded2Vanilla] Suppressed unresolvable tracked entry id {} for entity {}",
                    rawId, this.trackedEntity
                );
            }
        }

        if (this.trackedEntity != null) {
            try {
                this.trackedEntity.onDataTrackerUpdate(list);
            } catch (Throwable t) {
                Modded2Vanilla.LOGGER.debug(
                    "[Modded2Vanilla] Handled onDataTrackerUpdate exception for entity {}: {}",
                    this.trackedEntity, t.getMessage()
                );
            }
        }
    }

    @Inject(method = "copyToFrom", at = @At("HEAD"), cancellable = true, require = 0)
    private void modded2Vanilla$guardCopy(
        DataTracker.Entry<?> to, DataTracker.SerializedEntry<?> from, CallbackInfo ci
    ) {
        if (!IsolatorState.isCompensatingDataTracker()) {
            return;
        }
        if (to == null || from == null || !sameHandler(to.getData(), from.handler())) {
            if (this.trackedEntity != null) {
                AdjustmentTracker.recordClass(this.trackedEntity.getClass());
            }
            Modded2Vanilla.LOGGER.debug(
                "[Modded2Vanilla] Suppressed incompatible entity data update for {}: {}",
                this.trackedEntity, from
            );
            ci.cancel();
        }
    }

    private DataTracker.Entry<?> resolveMatchingEntry(int rawId, DataTracker.SerializedEntry<?> serializedEntry) {
        // 1. Direct index match
        if (isValidIndex(rawId) && sameHandler(this.entries[rawId].getData(), serializedEntry.handler())) {
            return this.entries[rawId];
        }

        // 2. Relative offset checks for common modded field shifts (+1, -1, +2, -2, +3, -3)
        final int[] offsets = { 1, -1, 2, -2, 3, -3, 4, -4 };
        for (final int offset : offsets) {
            final int candidate = rawId + offset;
            if (isValidIndex(candidate) && sameHandler(this.entries[candidate].getData(), serializedEntry.handler())) {
                return this.entries[candidate];
            }
        }

        // 3. Closest matching handler scan
        DataTracker.Entry<?> bestMatch = null;
        int minDistance = Integer.MAX_VALUE;

        for (int i = 0; i < this.entries.length; i++) {
            final DataTracker.Entry<?> entry = this.entries[i];
            if (entry != null && sameHandler(entry.getData(), serializedEntry.handler())) {
                int dist = Math.abs(i - rawId);
                if (dist < minDistance) {
                    minDistance = dist;
                    bestMatch = entry;
                }
            }
        }

        return bestMatch;
    }

    @SuppressWarnings("unchecked")
    private static <T> void copySafely(DataTracker.Entry<T> to, DataTracker.SerializedEntry<?> from) {
        if (sameHandler(to.getData(), from.handler())) {
            to.set((T) from.value());
        }
    }

    private boolean isValidIndex(int id) {
        return id >= 0 && id < this.entries.length && this.entries[id] != null;
    }

    private static boolean sameHandler(TrackedData<?> data, TrackedDataHandler<?> handler) {
        return data != null && Objects.equals(data.dataType(), handler);
    }
}
