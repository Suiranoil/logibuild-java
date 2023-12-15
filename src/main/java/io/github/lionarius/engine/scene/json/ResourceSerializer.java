package io.github.lionarius.engine.scene.json;

import com.google.gson.*;
import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.util.ReflectionUtil;

import java.lang.reflect.Type;

public class ResourceSerializer implements JsonSerializer<Resource>, JsonDeserializer<Resource> {
    @Override
    public JsonElement serialize(Resource src, Type typeOfSrc, JsonSerializationContext context) {
        var obj = new JsonObject();

        obj.add("type", context.serialize(src.getClass().getSimpleName()));
        obj.add("name", context.serialize(src.getResourceName()));

        return obj;
    }

    @Override
    public Resource deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var obj = json.getAsJsonObject();
        var type = ReflectionUtil.getResourceClass(obj.get("type").getAsString());

        return Logibuild.getInstance().getResourceManager().get(type, obj.get("name").getAsString());
    }
}
