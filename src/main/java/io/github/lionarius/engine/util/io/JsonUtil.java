package io.github.lionarius.engine.util.io;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.json.GameObjectSerializer;
import io.github.lionarius.engine.scene.json.SceneJsonIO;
import lombok.experimental.UtilityClass;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@UtilityClass
public class JsonUtil {
    private static final Gson SCENE_GSON = new GsonBuilder().setPrettyPrinting()
            .registerTypeAdapter(
                    GameObject.class,
                    new GameObjectSerializer()
            ).registerTypeAdapter(
                    Scene.class,
                    new SceneJsonIO()
            ).create();

    public static Scene loadSceneFromFile(String path) {
        try {
            var file = new File(path);
            var val = Files.readString(file.toPath());
            return deserializeScene(val);
        } catch (IOException e) {
            return null;
        }
    }

    public static void writeSceneToFile(String path, Scene scene) {
        try {
            var file = new File(path);
            var val = serializeScene(scene);
            Files.write(file.toPath(), val.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Scene deserializeScene(String json) {
        return SCENE_GSON.fromJson(json, Scene.class);
    }

    public static String serializeScene(Scene value) {
        return SCENE_GSON.toJson(value);
    }
}
