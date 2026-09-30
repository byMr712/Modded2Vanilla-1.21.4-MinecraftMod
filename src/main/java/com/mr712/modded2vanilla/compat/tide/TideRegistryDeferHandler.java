package com.mr712.modded2vanilla.compat.tide;

import com.mr712.modded2vanilla.Modded2Vanilla;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public final class TideRegistryDeferHandler {

    private static final List<Pair<RegistryKey<Item>, Item>> PENDING_ITEMS = new ArrayList<>();
    private static final List<Pair<RegistryKey<Block>, Block>> PENDING_BLOCKS = new ArrayList<>();
    private static final List<Pair<RegistryKey<EntityType<?>>, EntityType<?>>> PENDING_ENTITIES = new ArrayList<>();
    private static final List<Pair<RegistryKey<SoundEvent>, SoundEvent>> PENDING_SOUNDS = new ArrayList<>();
    private static final List<Pair<Object, Object>> PENDING_BLOCK_ENTITIES = new ArrayList<>();

    private static boolean flushed = false;

    @SuppressWarnings("unchecked")
    public static synchronized Object deferItem(Object key, Object item) {
        if (flushed) {
            return Registry.register(Registries.ITEM, (RegistryKey<Item>) key, (Item) item);
        }
        PENDING_ITEMS.add(new Pair<>((RegistryKey<Item>) key, (Item) item));
        return item;
    }

    @SuppressWarnings("unchecked")
    public static synchronized Object deferBlock(Object key, Object block) {
        if (flushed) {
            return Registry.register(Registries.BLOCK, (RegistryKey<Block>) key, (Block) block);
        }
        PENDING_BLOCKS.add(new Pair<>((RegistryKey<Block>) key, (Block) block));
        return block;
    }

    @SuppressWarnings("unchecked")
    public static synchronized Object deferEntityType(Object key, Object entityType) {
        if (flushed) {
            return Registry.register(Registries.ENTITY_TYPE, (RegistryKey<EntityType<?>>) key, (EntityType<?>) entityType);
        }
        PENDING_ENTITIES.add(new Pair<>((RegistryKey<EntityType<?>>) key, (EntityType<?>) entityType));
        return entityType;
    }

    @SuppressWarnings("unchecked")
    public static synchronized Object deferSoundEvent(Object key, Object soundEvent) {
        if (flushed) {
            return Registry.register(Registries.SOUND_EVENT, (RegistryKey<SoundEvent>) key, (SoundEvent) soundEvent);
        }
        PENDING_SOUNDS.add(new Pair<>((RegistryKey<SoundEvent>) key, (SoundEvent) soundEvent));
        return soundEvent;
    }

    @SuppressWarnings("unchecked")
    public static synchronized Object deferBlockEntity(Object key, Object blockEntityType) {
        if (flushed) {
            if (key instanceof RegistryKey<?> regKey) {
                return Registry.register(Registries.BLOCK_ENTITY_TYPE, (RegistryKey<BlockEntityType<?>>) regKey, (BlockEntityType<?>) blockEntityType);
            } else if (key instanceof Identifier id) {
                return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, (BlockEntityType<?>) blockEntityType);
            } else {
                return Registry.register(Registries.BLOCK_ENTITY_TYPE, String.valueOf(key), (BlockEntityType<?>) blockEntityType);
            }
        }
        PENDING_BLOCK_ENTITIES.add(new Pair<>(key, blockEntityType));
        return blockEntityType;
    }

    @SuppressWarnings("unchecked")
    public static synchronized void flush() {
        if (flushed) {
            return;
        }
        flushed = true;
        if (PENDING_BLOCKS.isEmpty() && PENDING_ITEMS.isEmpty() && PENDING_ENTITIES.isEmpty() && PENDING_SOUNDS.isEmpty() && PENDING_BLOCK_ENTITIES.isEmpty()) {
            return;
        }
        Modded2Vanilla.LOGGER.info("Flushing deferred Tide registrations (Blocks: {}, Items: {}, Entities: {}, Sounds: {}, BlockEntities: {}) after vanilla initialization...",
            PENDING_BLOCKS.size(), PENDING_ITEMS.size(), PENDING_ENTITIES.size(), PENDING_SOUNDS.size(), PENDING_BLOCK_ENTITIES.size());

        for (final Pair<RegistryKey<Block>, Block> entry : PENDING_BLOCKS) {
            Registry.register(Registries.BLOCK, entry.key(), entry.value());
        }
        PENDING_BLOCKS.clear();

        for (final Pair<RegistryKey<Item>, Item> entry : PENDING_ITEMS) {
            Registry.register(Registries.ITEM, entry.key(), entry.value());
        }
        PENDING_ITEMS.clear();

        for (final Pair<RegistryKey<EntityType<?>>, EntityType<?>> entry : PENDING_ENTITIES) {
            Registry.register(Registries.ENTITY_TYPE, entry.key(), entry.value());
        }
        PENDING_ENTITIES.clear();

        for (final Pair<RegistryKey<SoundEvent>, SoundEvent> entry : PENDING_SOUNDS) {
            Registry.register(Registries.SOUND_EVENT, entry.key(), entry.value());
        }
        PENDING_SOUNDS.clear();

        for (final Pair<Object, Object> entry : PENDING_BLOCK_ENTITIES) {
            Object key = entry.key();
            Object value = entry.value();
            if (key instanceof RegistryKey<?> regKey) {
                Registry.register(Registries.BLOCK_ENTITY_TYPE, (RegistryKey<BlockEntityType<?>>) regKey, (BlockEntityType<?>) value);
            } else if (key instanceof Identifier id) {
                Registry.register(Registries.BLOCK_ENTITY_TYPE, id, (BlockEntityType<?>) value);
            } else {
                Registry.register(Registries.BLOCK_ENTITY_TYPE, String.valueOf(key), (BlockEntityType<?>) value);
            }
        }
        PENDING_BLOCK_ENTITIES.clear();
    }

    private record Pair<K, V>(K key, V value) {
    }
}
