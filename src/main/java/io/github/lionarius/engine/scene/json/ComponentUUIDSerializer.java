package io.github.lionarius.engine.scene.json;

import com.google.gson.JsonElement;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import io.github.lionarius.engine.scene.Component;

import java.lang.reflect.Type;

public class ComponentUUIDSerializer implements JsonSerializer<Component> {
    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUuid());
    }
}
