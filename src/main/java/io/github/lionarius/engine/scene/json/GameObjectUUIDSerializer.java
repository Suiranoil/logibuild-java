package io.github.lionarius.engine.scene.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.lionarius.engine.scene.GameObject;

import java.lang.reflect.Type;

public class GameObjectUUIDSerializer implements JsonSerializer<GameObject> {
    @Override
    public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUuid());
    }
}
