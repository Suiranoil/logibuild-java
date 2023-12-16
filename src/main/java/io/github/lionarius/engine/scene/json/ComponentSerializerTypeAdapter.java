package io.github.lionarius.engine.scene.json;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.util.ReflectionUtil;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.javatuples.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ComponentSerializerTypeAdapter extends TypeAdapter<Component> {
    private static final Logger LOGGER = LoggerFactory.getLogger("Serialization");

    private final Gson gson;
    @Setter
    private Map<UUID, List<Pair<UUID, Field>>> wiring = null;

    @Override
    public void write(JsonWriter out, Component component) throws IOException {
        if (component == null) {
            out.nullValue();
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", component.getClass().getSimpleName());
        jsonObject.addProperty("uuid", component.getUuid().toString());

        var fields = ReflectionUtil.getSerializableComponentFields(component.getClass());
        for (var fieldData : fields) {
            var field = fieldData.field();
            try {
                var value = field.get(component);
                if (value == null)
                    continue;

                jsonObject.add(field.getName(), this.gson.toJsonTree(value));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.gson.toJson(jsonObject, out);
    }

    @Override
    public Component read(JsonReader in) throws IOException {
        JsonObject obj = this.gson.fromJson(in, JsonObject.class);
        var type = obj.get("type").getAsString();
        var clazz = ReflectionUtil.getComponentClass(type);
        if (clazz == null) {
            LOGGER.warn("Could not find component with type {}", type);
            return null;
        }

        List<Pair<UUID, Field>> componentWiring = new ArrayList<>();
        var fields = ReflectionUtil.getSerializableComponentFields(clazz);
        for (var fieldData : fields) {
            var field = fieldData.field();
            if (Component.class.isAssignableFrom(field.getType())) {
                if (obj.has(field.getName())) {
                    var uuid = this.gson.fromJson(obj.get(field.getName()), UUID.class);
                    componentWiring.add(Pair.with(uuid, field));
                }
                obj.remove(field.getName());
            }
        }

        var component = this.gson.fromJson(obj, clazz);
        if (!componentWiring.isEmpty() && this.wiring != null)
            this.wiring.put(component.getUuid(), componentWiring);

        return component;
    }
}
