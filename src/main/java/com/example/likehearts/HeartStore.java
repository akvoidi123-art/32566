package com.example.likehearts;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

/** Хранит "ник -> тип сердечка (1-3)" и сохраняет это в config/likehearts.json. */
public final class HeartStore {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Map<String, Integer> HEARTS = new ConcurrentHashMap<>();

    private HeartStore() {
    }

    private static Path file() {
        return FabricLoader.getInstance().getConfigDir().resolve("likehearts.json");
    }

    public static void load() {
        Path path = file();
        if (!Files.exists(path)) {
            return;
        }
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            Map<String, Integer> loaded = GSON.fromJson(reader, new TypeToken<Map<String, Integer>>() { }.getType());
            HEARTS.clear();
            if (loaded != null) {
                for (Map.Entry<String, Integer> e : loaded.entrySet()) {
                    int type = e.getValue() == null ? 0 : e.getValue();
                    if (type >= 1 && type <= 3) {
                        HEARTS.put(e.getKey().toLowerCase(Locale.ROOT), type);
                    }
                }
            }
        } catch (IOException | RuntimeException e) {
            LikeHeartsClientMod.LOGGER.warn("Не удалось прочитать {}", path, e);
        }
    }

    private static void save() {
        Path path = file();
        try (Writer writer = Files.newBufferedWriter(path, StandardCharsets.UTF_8)) {
            GSON.toJson(new TreeMap<>(HEARTS), writer);
        } catch (IOException e) {
            LikeHeartsClientMod.LOGGER.warn("Не удалось сохранить {}", path, e);
        }
    }

    /** type 0 — убрать сердечко. */
    public static void set(String nick, int type) {
        String key = nick.toLowerCase(Locale.ROOT);
        if (type <= 0) {
            HEARTS.remove(key);
        } else {
            HEARTS.put(key, type);
        }
        save();
    }

    public static Map<String, Integer> all() {
        return Collections.unmodifiableMap(new TreeMap<>(HEARTS));
    }

    /**
     * Ищет в отображаемом имени (например "[VIP] Notch" или "Notch") ник, которому назначено
     * сердечко. Возвращает 1-3 или 0, если сердечка нет.
     */
    public static int typeFor(String displayText) {
        if (HEARTS.isEmpty() || displayText == null) {
            return 0;
        }
        for (String token : displayText.split("[^A-Za-z0-9_]+")) {
            if (token.isEmpty()) {
                continue;
            }
            Integer type = HEARTS.get(token.toLowerCase(Locale.ROOT));
            if (type != null) {
                return type;
            }
        }
        return 0;
    }
}
