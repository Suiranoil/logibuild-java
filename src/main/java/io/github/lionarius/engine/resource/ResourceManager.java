package io.github.lionarius.engine.resource;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ResourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ResourceLoader");

    @NonNull
    private final String folder;
    private final Map<Class<?>, ResourceData<?>> resources = new HashMap<>();

    public File getResourceFolder() {
        return new File(this.folder);
    }

    public <T extends Resource> void register(@NonNull Class<T> clazz, @NonNull ResourceLoader<T> loader) {
        this.resources.put(clazz, new ResourceData<>(loader));
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String name) {
        return this.get(clazz, this.folder, name, null);
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String name, Object parameters) {
        return this.get(clazz, this.folder, name, parameters);
    }

    private <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String folder, @NonNull String name) {
        return this.get(clazz, folder, name, null);
    }

    private <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String folder, @NonNull String name, Object parameters) {
        var data = this.getAssetData(clazz);
        File file;
        if (folder.isEmpty())
            file = new File(name);
        else
            file = new File(folder, name);
        var path = file.getAbsolutePath();

        if (data.getCache().containsKey(path))
            return data.getCache().get(path);

        T resource = null;
        try {
            resource = data.getLoader().loadFromFile(name, path, parameters);
        } catch (Exception ignored) {
        }

        if (resource != null) {
            data.getCache().put(path, resource);
            resource.setResourceName(name);
        } else
            LOGGER.warn("Could not load resource {}", name);

        return resource;
    }

    public <T extends Resource> void invalidate(@NonNull Class<T> clazz, @NonNull String name) {
        this.invalidate(clazz, this.folder, name);
    }

    private <T extends Resource> void invalidate(@NonNull Class<T> clazz, @NonNull String folder, @NonNull String name) {
        var data = this.getAssetData(clazz);
        var file = new File(folder, name);
        var path = file.getAbsolutePath();

        var resource = data.getCache().remove(path);
        if (resource != null)
            resource.close();
    }

    @SuppressWarnings("unchecked")
    private <T extends Resource> ResourceData<T> getAssetData(Class<T> clazz) {
        if (!this.resources.containsKey(clazz))
            throw new IllegalArgumentException("Asset loader for '" + clazz.getSimpleName() + "' is not registered");

        return (ResourceData<T>) this.resources.get(clazz);
    }

    @Getter
    @RequiredArgsConstructor
    private static class ResourceData<T extends Resource> {
        private final ResourceLoader<T> loader;
        private final Map<String, T> cache = new HashMap<>();
    }
}
