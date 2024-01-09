package io.github.lionarius.engine.resource.scene;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.util.io.JsonUtil;

import java.io.IOException;

public class SceneLoader implements ResourceLoader<Scene> {
    @Override
    public Scene loadFromFile(String name, String filepath, Object parameters) throws IOException {
        return JsonUtil.loadSceneFromFile(filepath);
    }
}
