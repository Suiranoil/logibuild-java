package io.github.lionarius.engine.scene.json;

import com.google.gson.*;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.util.ReflectionUtil;
import org.javatuples.Pair;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;

public class SceneJsonIO implements JsonDeserializer<Scene>, JsonSerializer<Scene> {
    private static final Gson COMPONENT_FIELDS_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(
                    Component.class,
                    new ComponentUUIDSerializer()
            ).registerTypeHierarchyAdapter(
                    Resource.class,
                    new ResourceSerializer()
            ).create();

    private static final ComponentSerializerTypeAdapter COMPONENT_TYPE_ADAPTER = new ComponentSerializerTypeAdapter(COMPONENT_FIELDS_GSON);

    private static final Gson COMPONENT_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(
                    Component.class,
                    COMPONENT_TYPE_ADAPTER
            ).create();

    private static final Gson HIERARCHY_GSON = new GsonBuilder()
            .registerTypeAdapter(
                    GameObject.class,
                    new GameObjectUUIDSerializer()
            ).enableComplexMapKeySerialization().create();

    @Override
    public JsonElement serialize(Scene src, Type typeOfSrc, JsonSerializationContext context) {
        var hierarchy = src.getHierarchy();
        var res = new JsonObject();
        var objects = new JsonObject();
        var components = new JsonObject();
        for (var gameObject : hierarchy) {
            objects.add(gameObject.getUuid().toString(), context.serialize(gameObject));
            for (var component : gameObject.getComponents())
                components.add(component.getUuid().toString(), COMPONENT_GSON.toJsonTree(component));
        }
        res.add("components", components);
        res.add("gameObjects", objects);
        res.add("hierarchy", HIERARCHY_GSON.toJsonTree(hierarchy));
        if (src.getSelectedGameObject() != null)
            res.addProperty("selectedGameObject", String.valueOf(src.getSelectedGameObject().getUuid()));

        return res;
    }

    @Override
    public Scene deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        var sceneJson = json.getAsJsonObject();
        var components = deserializeComponents(sceneJson.getAsJsonObject("components"));
        var objects = deserializeGameObjects(sceneJson.getAsJsonObject("gameObjects"), components, context);

        return deserializeScene(sceneJson, objects, context);
    }

    private static Scene deserializeScene(JsonObject sceneJson, Map<UUID, GameObject> objects, JsonDeserializationContext context) {
        var scene = new Scene();
        var hierarchyJson = sceneJson.get("hierarchy").getAsJsonObject();

        for (var object : objects.values())
            scene.addGameObject(object);

        for (var entry : hierarchyJson.get("children").getAsJsonObject().entrySet()) {
            if (Objects.equals(entry.getKey(), "null"))
                continue;

            var parentUuid = UUID.fromString(entry.getKey());
            UUID[] childrenUuids = context.deserialize(entry.getValue(), UUID[].class);
            for (var childUuid : childrenUuids)
                objects.get(parentUuid).addChild(objects.get(childUuid));
        }

        scene.getHierarchy().processChanges();

        var selectedGameObject = sceneJson.get("selectedGameObject");
        if (selectedGameObject != null)
            scene.setSelectedGameObject(objects.get(UUID.fromString(selectedGameObject.getAsString())));

        return scene;
    }

    private static Map<UUID, Component> deserializeComponents(JsonObject componentsJson) {
        Map<UUID, Component> components = new HashMap<>();

        Map<UUID, List<Pair<UUID, Field>>> wiring = new HashMap<>();
        COMPONENT_TYPE_ADAPTER.setWiring(wiring);
        for (var entry : componentsJson.entrySet()) {
            var uuid = UUID.fromString(entry.getKey());
            var component = COMPONENT_GSON.fromJson(entry.getValue().getAsJsonObject(), Component.class);
            if (component == null)
                continue;

            components.put(uuid, component);
        }
        COMPONENT_TYPE_ADAPTER.setWiring(null);

        for (var entry : wiring.entrySet()) {
            var component = components.get(entry.getKey());
            var wires = entry.getValue();
            for (var wire : wires) {
                var wiredComponent = components.get(wire.getValue0());
                var field = wire.getValue1();

                ReflectionUtil.setField(field, component, wiredComponent);
            }
        }

        return components;
    }

    private static Map<UUID, GameObject> deserializeGameObjects(JsonObject gameObjectsJson, Map<UUID, Component> components, JsonDeserializationContext context) {
        Map<UUID, GameObject> gameObjects = new LinkedHashMap<>();

        for (var entry : gameObjectsJson.entrySet()) {
            var uuid = UUID.fromString(entry.getKey());
            var value = entry.getValue().getAsJsonObject();

            UUID[] wiredComponentsUUIDs = context.deserialize(value.get("components"), UUID[].class);
            value.remove("components");

            var object = (GameObject) context.deserialize(value, GameObject.class);
            if (object == null)
                continue;

            var wiredComponents = Arrays.stream(wiredComponentsUUIDs).map(components::get).toList();

            gameObjects.put(uuid, object);
            GameObject.fillComponents(object, wiredComponents);
        }

        return gameObjects;
    }
}
