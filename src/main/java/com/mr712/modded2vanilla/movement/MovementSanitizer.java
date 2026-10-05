package com.mr712.modded2vanilla.movement;

import com.mr712.modded2vanilla.isolator.MovementIsolator;

public final class MovementSanitizer {

    private MovementSanitizer() {
    }

    public static void detectAndRecordMovementMod() {
        MovementIsolator.detectAndRecordCallerMod();
    }
}
