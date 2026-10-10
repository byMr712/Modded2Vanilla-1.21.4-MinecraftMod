package com.mr712.modded2vanilla.snapshot;

import com.mr712.modded2vanilla.MrModded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import it.unimi.dsi.fastutil.objects.Reference2IntOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;

public final class VanillaBlockStateSnapshot {

    private static BlockState[] VANILLA_STATES = null;
    private static Reference2IntOpenHashMap<BlockState> STATE_TO_RAW_ID = null;
    private static volatile boolean captured = false;

    private VanillaBlockStateSnapshot() {
    }

    public static synchronized void capture() {
        if (captured) {
            return;
        }
        captured = true;

        int size = Block.STATE_IDS.size();
        VANILLA_STATES = new BlockState[size];
        STATE_TO_RAW_ID = new Reference2IntOpenHashMap<>(size);
        STATE_TO_RAW_ID.defaultReturnValue(-1);

        for (int i = 0; i < size; i++) {
            BlockState state = Block.STATE_IDS.get(i);
            VANILLA_STATES[i] = state;
            if (state != null) {
                STATE_TO_RAW_ID.put(state, i);
            }
        }
        MrModded2Vanilla.LOGGER.info("Captured pure vanilla block state snapshot ({} canonical states).", VANILLA_STATES.length);
    }

    public static BlockState resolveState(int rawId) {
        if (!IsolatorState.isIsolating() || VANILLA_STATES == null) {
            BlockState normal = Block.STATE_IDS.get(rawId);
            return normal != null ? normal : Blocks.AIR.getDefaultState();
        }

        if (rawId >= 0 && rawId < VANILLA_STATES.length) {
            BlockState canonical = VANILLA_STATES[rawId];
            if (canonical != null) {
                return canonical;
            }
        }

        BlockState fallback = Block.STATE_IDS.get(rawId);
        return fallback != null ? fallback : Blocks.AIR.getDefaultState();
    }

    public static int getRawId(BlockState state) {
        if (!IsolatorState.isIsolating() || STATE_TO_RAW_ID == null || state == null) {
            return Block.STATE_IDS.getRawId(state);
        }

        int rawId = STATE_TO_RAW_ID.getInt(state);
        if (rawId >= 0) {
            return rawId;
        }

        return Block.STATE_IDS.getRawId(state);
    }

    public static int getCanonicalCount() {
        return VANILLA_STATES != null ? VANILLA_STATES.length : 0;
    }
}
