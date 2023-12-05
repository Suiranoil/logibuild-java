package io.github.lionarius.engine.resource;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ResourceManager {
    @NonNull
    private final String folder;
    private final Map<Class<?>, AssetData<?>> assets = new HashMap<>();

    public <T extends Resource> void register(@NonNull Class<T> clazz, @NonNull ResourceLoader<T> loader) {
        this.assets.put(clazz, new AssetData<>(loader));
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String name) {
        return this.get(clazz, this.folder, name);
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String folder, @NonNull String name) {
        var data = this.getAssetData(clazz);
        var file = new File(folder, name);
        var path = file.getAbsolutePath();

        if (data.getCache().containsKey(path))
            return data.getCache().get(path);

        try {
            var asset = data.getLoader().loadFromFile(path);
            data.getCache().put(path, asset);
            return asset;
        } catch (IOException e) {
            throw new IllegalArgumentException("Could not find asset at " + file.getPath());
        }
    }

    public <T extends Resource> void invalidate(@NonNull Class<T> clazz, @NonNull String name) {
        this.invalidate(clazz, this.folder, name);
    }

    public <T extends Resource> void invalidate(@NonNull Class<T> clazz, @NonNull String folder, @NonNull String name) {
        var data = this.getAssetData(clazz);
        var file = new File(folder, name);
        var path = file.getAbsolutePath();

        var resource = data.getCache().remove(path);
        if (resource != null)
            resource.close();
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> AssetData<T> getAssetData(Class<T> clazz) {
        if (!this.assets.containsKey(clazz))
            throw new IllegalArgumentException("Asset loader for '" + clazz.getSimpleName() + "' is not registered");

        return (AssetData<T>) this.assets.get(clazz);
    }

    @Getter
    @RequiredArgsConstructor
    private static class AssetData<T extends Resource> {
        private final ResourceLoader<T> loader;
        private final Map<String, T> cache = new HashMap<>();
    }
}
