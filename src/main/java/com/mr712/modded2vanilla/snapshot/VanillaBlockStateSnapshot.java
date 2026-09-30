package com.mr712.modded2vanilla.snapshot;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

import java.util.ArrayList;
import java.util.List;

public final class VanillaBlockStateSnapshot {

    private static BlockState[] VANILLA_STATES = null;
    private static volatile boolean captured = false;

    private VanillaBlockStateSnapshot() {
    }

    public static synchronized void capture() {
        if (captured) {
            return;
        }
        captured = true;

        List<BlockState> list = new ArrayList<>();
        for (BlockState state : Block.STATE_IDS) {
            if (state != null) {
                list.add(state);
            }
        }
        VANILLA_STATES = list.toArray(new BlockState[0]);
        Modded2Vanilla.LOGGER.info("Captured pure vanilla block state snapshot ({} canonical states).", VANILLA_STATES.length);
    }

    public static BlockState resolveState(int rawId) {
        if (!IsolatorState.isIsolating() || VANILLA_STATES == null) {
            BlockState normal = Block.STATE_IDS.get(rawId);
            return normal != null ? normal : Blocks.AIR.getDefaultState();
        }

        if (rawId >= 0 && rawId < VANILLA_STATES.length) {
            BlockState canonical = VANILLA_STATES[rawId];
            if (canonical != null) {
                BlockState current = Block.STATE_IDS.get(rawId);
                if (current != canonical) {
                    // Difference detected in state palette
                    AdjustmentTracker.recordClass(current != null ? current.getBlock().getClass() : Blocks.AIR.getClass());
                }
                return canonical;
            }
        }

        BlockState fallback = Block.STATE_IDS.get(rawId);
        return fallback != null ? fallback : Blocks.AIR.getDefaultState();
    }

    public static int getCanonicalCount() {
        return VANILLA_STATES != null ? VANILLA_STATES.length : 0;
    }
}
