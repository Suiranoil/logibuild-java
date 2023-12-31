package io.github.lionarius.engine.util;

import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.scene.Component;
import lombok.NonNull;
import lombok.experimental.UtilityClass;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UtilityClass
public class ReflectionUtil {
    private static final Map<String, Class<? extends Component>> ALL_COMPONENTS = new HashMap<>();
    private static final Map<Class<? extends Component>, List<SerializableFieldData>> COMPONENT_SERIALIZABLE_FIELDS = new HashMap<>();
    private static final Map<String, Class<? extends Resource>> ALL_RESOURCES = new HashMap<>();
    private static final Reflections REFLECTIONS = new Reflections("io.github.lionarius");

    static {
        //noinspection unchecked
        var allComponents = REFLECTIONS.get(
                Scanners.SubTypes.of(Component.class)
                        .asClass()
                        .filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
                        .map(clazz -> (Class<? extends Component>) clazz)
        );
        for (var clazz : allComponents) {
            if (clazz.isAnnotationPresent(HideComponent.class))
                continue;

            ALL_COMPONENTS.put(clazz.getSimpleName(), clazz);
            COMPONENT_SERIALIZABLE_FIELDS.put(
                    clazz,
                    ReflectionUtil.getAllSerializableFields(clazz)
                            .stream()
                            .map(field -> {
                                        if (!ReflectionUtil.isAccessible(field))
                                            field.setAccessible(true);
                                        return new SerializableFieldData(field, CaseUtil.toSentenceCase(field.getName()));
                                    }
                            ).toList());
        }

        var allResources = REFLECTIONS.getSubTypesOf(Resource.class);
        for (var clazz : allResources) {
            ALL_RESOURCES.put(clazz.getSimpleName(), clazz);
        }
    }

    public static boolean shareAncestor(@NonNull Class<?> clazz1, @NonNull Class<?> clazz2, @NonNull Class<?> stopClazz) {
        if (clazz1.isAssignableFrom(clazz2))
            return true;

        if (clazz2.isAssignableFrom(clazz1))
            return true;

        var super1 = clazz1.getSuperclass();

        if (super1 == stopClazz)
            return false;

        return ReflectionUtil.shareAncestor(super1, clazz2, stopClazz);
    }

    public static Iterable<SerializableFieldData> getSerializableComponentFields(@NonNull Class<? extends Component> clazz) {
        return COMPONENT_SERIALIZABLE_FIELDS.get(clazz);
    }

    public static Iterable<Class<? extends Component>> getAllComponentClasses() {
        return ALL_COMPONENTS.values();
    }

    public static Iterable<Class<? extends Resource>> getAllResourceClasses() {
        return ALL_RESOURCES.values();
    }

    public static Class<? extends Component> getComponentClass(String name) {
        return ALL_COMPONENTS.get(name);
    }

    public static Class<? extends Resource> getResourceClass(String name) {
        return ALL_RESOURCES.get(name);
    }

    public static void setField(@NonNull Field field, @NonNull Object object, Object value) {
        try {
            var isPrivate = !ReflectionUtil.isAccessible(field);

            if (isPrivate)
                field.setAccessible(true);

            field.set(object, value);

            if (isPrivate)
                field.setAccessible(false);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isAccessible(@NonNull Field field) {
        return field.isAccessible() || (!Modifier.isPublic(field.getModifiers())
                                        && !Modifier.isPublic(field.getDeclaringClass().getModifiers()));
    }

    private static boolean isSerializable(Field field) {
        return (field.isAnnotationPresent(SerializeField.class) || Modifier.isPublic(field.getModifiers()))
               && !Modifier.isStatic(field.getModifiers());
    }

    private static List<Field> getAllSerializableFields(@NonNull Class<? extends Component> clazz) {
        List<Field> fields = new ArrayList<>();
        var classFields = ReflectionUtils.get(ReflectionUtils.Fields.of(clazz));
        for (var field : classFields) {
            if (ReflectionUtil.isSerializable(field))
                fields.add(field);
        }

        return fields;
    }

    public record SerializableFieldData(Field field, String name) {
    }
}
