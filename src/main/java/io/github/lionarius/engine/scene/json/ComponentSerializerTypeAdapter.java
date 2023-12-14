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

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ComponentSerializerTypeAdapter extends TypeAdapter<Component> {
    private final Gson gson;
    @Setter
    private Map<UUID, List<Pair<UUID, Field>>> wiring = null;

    @Override
    public void write(JsonWriter out, Component value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", value.getClass().getSimpleName());
        jsonObject.addProperty("uuid", value.getUuid().toString());

        var fields = ReflectionUtil.getSerializableComponentFields(value.getClass());
        for (Field field : fields) {
            try {
                var isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate)
                    field.setAccessible(true);

                var fieldValue = field.get(value);
                if (fieldValue == null)
                    continue;

                jsonObject.add(field.getName(), this.gson.toJsonTree(fieldValue));

                if (isPrivate)
                    field.setAccessible(false);

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        this.gson.toJson(jsonObject, out);
    }

    @Override
    public Component read(JsonReader in) throws IOException {
        JsonObject obj = this.gson.fromJson(in, JsonObject.class);
        var type = ReflectionUtil.getComponentClass(obj.get("type").getAsString());

        List<Pair<UUID, Field>> wiring = new ArrayList<>();
        var fields = ReflectionUtil.getSerializableComponentFields(type);
        for (var field : fields) {
            if (Component.class.isAssignableFrom(field.getType())) {
                if (this.wiring != null && obj.has(field.getName())) {
                    var uuid = this.gson.fromJson(obj.get(field.getName()), UUID.class);
                    wiring.add(Pair.with(uuid, field));
                }
                obj.remove(field.getName());
            }
        }

        var component = this.gson.fromJson(obj, type);
        if (!wiring.isEmpty())
            this.wiring.put(component.getUuid(), wiring);

        return component;
    }
}
