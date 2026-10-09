package com.mr712.modded2vanilla.component;

import com.mr712.modded2vanilla.state.IsolatorState;
import com.mr712.modded2vanilla.tracker.AdjustmentTracker;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public final class ComponentSanitizer {

    private ComponentSanitizer() {
    }

    public static ItemStack sanitizeForVanillaNetwork(ItemStack stack) {
        if (stack == null || stack.isEmpty() || !IsolatorState.isIsolating()) {
            return stack;
        }
        if (IsolatorState.isModdedServer()) {
            // Modded server supports modded components, don't strip
            return stack;
        }

        DataComponentMap components = stack.getComponents();
        if (components == null || components.isEmpty()) {
            return stack;
        }

        boolean hasModdedComponent = false;
        for (DataComponentType<?> type : components.keySet()) {
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
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
        for (DataComponentType<?> type : components.keySet()) {
            Identifier id = BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(type);
            if (id != null && !"minecraft".equals(id.getNamespace())) {
                copy.remove(type);
            }
        }
        return copy;
    }
}
