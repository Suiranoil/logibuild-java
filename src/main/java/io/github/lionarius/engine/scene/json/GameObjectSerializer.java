package io.github.lionarius.engine.scene.json;

import com.google.gson.*;
import io.github.lionarius.engine.scene.GameObject;

import java.lang.reflect.Type;

public class GameObjectSerializer implements JsonSerializer<GameObject> {
    @Override
    public JsonElement serialize(GameObject src, Type typeOfSrc, JsonSerializationContext context) {
        var object = new JsonObject();
        object.add("name", context.serialize(src.getName()));
        object.add("uuid", context.serialize(src.getUuid()));

        var components = new JsonArray();
        object.add("components", components);
        for (var component : src.getComponents())
            components.add(context.serialize(component.getUuid()));

        return object;
    }
}
