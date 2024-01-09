package io.github.lionarius.engine.resource;

import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import io.github.lionarius.engine.util.io.StreamUtil;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class ResourceManager {
    private static final Logger LOGGER = LoggerFactory.getLogger("ResourceLoader");

    @NonNull @Getter
    private final ResourceStreamProvider streamProvider;
    private final Map<Class<?>, ResourceData<?>> resources = new HashMap<>();

    public File getResourceFolder() {
        return new File(this.streamProvider.getBase());
    }

    public <T extends Resource> void register(@NonNull Class<T> clazz, @NonNull ResourceLoader<T> loader) {
        this.resources.put(clazz, new ResourceData<>(loader));
    }

    public ByteBuffer getRaw(@NonNull String name) {
        ByteBuffer data = null;

        try (var stream = this.streamProvider.getStream(name)) {
            data = StreamUtil.readStreamToBuffer(stream);
        } catch (IOException e) {
            LOGGER.error(e.toString());
        }

        return data;
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String name) {
        return this.get(clazz, name, null);
    }

    public <T extends Resource> T get(@NonNull Class<T> clazz, @NonNull String name, Object parameters) {
        var data = this.getAssetData(clazz);
        if (data.getCache().containsKey(name))
            return data.getCache().get(name);

        T resource = null;
        try {
            resource = data.getLoader().loadFromFile(name, this.streamProvider, parameters);
        } catch (Exception e) {
            LOGGER.error(e.toString());
        }

        if (resource != null) {
            data.getCache().put(name, resource);
            resource.setResourceName(name);
        } else
            LOGGER.warn("Could not load resource {}", name);

        return resource;
    }

    private <T extends Resource> void invalidate(@NonNull Class<T> clazz, @NonNull String name) {
        var data = this.getAssetData(clazz);

        var resource = data.getCache().remove(name);
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
