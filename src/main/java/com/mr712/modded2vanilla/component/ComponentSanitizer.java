package com.mr712.modded2vanilla.component;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public final class ComponentSanitizer {

    private ComponentSanitizer() {
    }

    public static ItemStack sanitizeForVanillaNetwork(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !IsolatorState.isIsolating()) {
            return stack;
        }
        if (IsolatorState.isModdedServer()) {
            return stack;
        }

        ComponentMap components = stack.getComponents();
        if (components == null || components.isEmpty()) {
            return stack;
        }

        boolean hasModdedComponent = false;
        for (ComponentType<?> type : components.getTypes()) {
            Identifier id = Registries.DATA_COMPONENT_TYPE.getId(type);
            if (id != null && !"minecraft".equals(id.getNamespace())) {
                hasModdedComponent = true;
                AdjustmentTracker.recordMod(id.getNamespace());
                break;
            }
        }

        if (!hasModdedComponent) {
            return stack;
        }

        ItemStack copy = stack.copy();
        for (ComponentType<?> type : components.getTypes()) {
            Identifier id = Registries.DATA_COMPONENT_TYPE.getId(type);
            if (id != null && !"minecraft".equals(id.getNamespace())) {
                copy.set(type, null);
            }
        }
        return copy;
    }

    public static void sanitizeInPlace(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !IsolatorState.isIsolating() || IsolatorState.isModdedServer()) {
            return;
        }

        ComponentMap components = stack.getComponents();
        if (components == null || components.isEmpty()) {
            return;
        }

        for (ComponentType<?> type : components.getTypes()) {
            Identifier id = Registries.DATA_COMPONENT_TYPE.getId(type);
            if (id != null && !"minecraft".equals(id.getNamespace())) {
                stack.set(type, null);
                AdjustmentTracker.recordMod(id.getNamespace());
            }
        }
    }
}
