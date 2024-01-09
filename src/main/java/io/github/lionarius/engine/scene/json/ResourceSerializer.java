package io.github.lionarius.engine.scene.json;

import com.google.gson.*;
import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.util.ReflectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;

public class ResourceSerializer implements JsonSerializer<Resource>, JsonDeserializer<Resource> {
    private static final Logger LOGGER = LoggerFactory.getLogger("Serialization");

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
        var type = obj.get("type").getAsString();
        var name = obj.get("name").getAsString();

        var clazz = ReflectionUtil.getResourceClass(type);
        if (clazz == null) {
            LOGGER.warn("Could not find resource with type {}", type);
            return null;
        }

        return Logibuild.getInstance().getWorkspaceResourceManager().get(clazz, name);
    }
}
