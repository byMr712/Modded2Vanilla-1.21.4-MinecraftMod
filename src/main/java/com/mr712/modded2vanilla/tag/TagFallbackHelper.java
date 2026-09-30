package com.mr712.modded2vanilla.tag;

import com.mr712.modded2vanilla.Modded2Vanilla;
import com.mr712.modded2vanilla.state.IsolatorState;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public final class TagFallbackHelper {

    private TagFallbackHelper() {
    }

    public static void applyConventionalTagsFallback() {
        if (!IsolatorState.isIsolating()) {
            return;
        }

        try {
            // Block tags fallback
            populateBlockTagIfEmpty("c", "chests", List.of(Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.ENDER_CHEST, Blocks.BARREL));
            populateBlockTagIfEmpty("c", "ores", List.of(
                Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE,
                Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
                Blocks.COPPER_ORE, Blocks.DEEPSLATE_COPPER_ORE,
                Blocks.GOLD_ORE, Blocks.DEEPSLATE_GOLD_ORE, Blocks.NETHER_GOLD_ORE,
                Blocks.REDSTONE_ORE, Blocks.DEEPSLATE_REDSTONE_ORE,
                Blocks.EMERALD_ORE, Blocks.DEEPSLATE_EMERALD_ORE,
                Blocks.LAPIS_ORE, Blocks.DEEPSLATE_LAPIS_ORE,
                Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
                Blocks.NETHER_QUARTZ_ORE, Blocks.ANCIENT_DEBRIS
            ));

            // Item tags fallback
            populateItemTagIfEmpty("c", "shears", List.of(Items.SHEARS));
            populateItemTagIfEmpty("c", "chests", List.of(Items.CHEST, Items.TRAPPED_CHEST, Items.ENDER_CHEST, Items.BARREL));
            populateItemTagIfEmpty("c", "tools", List.of(
                Items.WOODEN_SWORD, Items.WOODEN_SHOVEL, Items.WOODEN_PICKAXE, Items.WOODEN_AXE, Items.WOODEN_HOE,
                Items.STONE_SWORD, Items.STONE_SHOVEL, Items.STONE_PICKAXE, Items.STONE_AXE, Items.STONE_HOE,
                Items.IRON_SWORD, Items.IRON_SHOVEL, Items.IRON_PICKAXE, Items.IRON_AXE, Items.IRON_HOE,
                Items.GOLDEN_SWORD, Items.GOLDEN_SHOVEL, Items.GOLDEN_PICKAXE, Items.GOLDEN_AXE, Items.GOLDEN_HOE,
                Items.DIAMOND_SWORD, Items.DIAMOND_SHOVEL, Items.DIAMOND_PICKAXE, Items.DIAMOND_AXE, Items.DIAMOND_HOE,
                Items.NETHERITE_SWORD, Items.NETHERITE_SHOVEL, Items.NETHERITE_PICKAXE, Items.NETHERITE_AXE, Items.NETHERITE_HOE,
                Items.SHEARS, Items.FISHING_ROD, Items.FLINT_AND_STEEL
            ));
        } catch (Throwable t) {
            Modded2Vanilla.LOGGER.debug("[Modded2Vanilla] Tag fallback notice: {}", t.getMessage());
        }
    }

    private static void populateBlockTagIfEmpty(String namespace, String path, List<Block> fallbackBlocks) {
        TagKey<Block> tagKey = TagKey.of(RegistryKeys.BLOCK, Identifier.of(namespace, path));
        Registry<Block> registry = Registries.BLOCK;
        Optional<RegistryEntryList.Named<Block>> tagOpt = registry.getOptional(tagKey);
        if (tagOpt.isEmpty() || tagOpt.get().size() == 0) {
            List<RegistryEntry<Block>> entries = new ArrayList<>();
            for (Block block : fallbackBlocks) {
                entries.add(registry.getEntry(block));
            }
            bindTagEntries(registry, tagKey, entries);
        }
    }

    private static void populateItemTagIfEmpty(String namespace, String path, List<Item> fallbackItems) {
        TagKey<Item> tagKey = TagKey.of(RegistryKeys.ITEM, Identifier.of(namespace, path));
        Registry<Item> registry = Registries.ITEM;
        Optional<RegistryEntryList.Named<Item>> tagOpt = registry.getOptional(tagKey);
        if (tagOpt.isEmpty() || tagOpt.get().size() == 0) {
            List<RegistryEntry<Item>> entries = new ArrayList<>();
            for (Item item : fallbackItems) {
                entries.add(registry.getEntry(item));
            }
            bindTagEntries(registry, tagKey, entries);
        }
    }

    @SuppressWarnings("unchecked")
    private static <T> void bindTagEntries(Registry<T> registry, TagKey<T> tagKey, List<RegistryEntry<T>> entries) {
        try {
            // 1. Try registry.getOrCreateEntryList(tagKey) reflectively
            for (Method method : registry.getClass().getMethods()) {
                if ("getOrCreateEntryList".equals(method.getName()) && method.getParameterCount() == 1) {
                    method.setAccessible(true);
                    Object named = method.invoke(registry, tagKey);
                    if (named != null) {
                        for (Method m : named.getClass().getDeclaredMethods()) {
                            if (m.getParameterCount() == 1 && List.class.isAssignableFrom(m.getParameterTypes()[0])) {
                                m.setAccessible(true);
                                m.invoke(named, entries);
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Throwable ignored) {
        }

        try {
            // 2. Try registry method taking Map<TagKey, List<RegistryEntry>>
            for (Method method : registry.getClass().getMethods()) {
                if (method.getParameterCount() == 1 && Map.class.isAssignableFrom(method.getParameterTypes()[0])) {
                    method.setAccessible(true);
                    method.invoke(registry, Map.of(tagKey, entries));
                    return;
                }
            }
        } catch (Throwable ignored) {
        }
    }
}
