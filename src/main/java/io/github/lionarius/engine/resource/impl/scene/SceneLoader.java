package io.github.lionarius.engine.resource.impl.scene;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.util.io.JsonUtil;

import java.io.IOException;

public class SceneLoader implements ResourceLoader<Scene> {
    @Override
    public Scene loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        try (var stream = streamProvider.getStream(name)) {
            return JsonUtil.loadSceneFromStream(stream);
        }
    }
}
