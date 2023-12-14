package io.github.lionarius.engine.scene.json;

import com.google.gson.*;
import io.github.lionarius.engine.scene.Component;

import java.lang.reflect.Type;

public class ComponentUUIDSerializer implements JsonSerializer<Component> {
    private static final Gson GSON = new GsonBuilder().create();

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        return context.serialize(src.getUuid());
    }

//    @Override
//    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
//       if (json.isJsonObject())
//           return context.deserialize(json, Component.class);
//
//        return null;
//    }
}
