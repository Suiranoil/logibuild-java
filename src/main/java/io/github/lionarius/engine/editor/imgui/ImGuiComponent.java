package io.github.lionarius.engine.editor.imgui;

import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImString;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.MinMax;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.util.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.joml.Quaternionf;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;

@UtilityClass
public class ImGuiComponent {
    public static void drawProperties(Component component) {
        ImGui.pushID(component.getUuid().hashCode());
        try {
            if (ImGuiComponent.drawHeader(component)) {
                ImGui.indent();
                var fields = ReflectionUtil.getSerializableComponentFields(component.getClass());
                for (var fieldData : fields) {
                    var field = fieldData.field();
                    var minAnnotation = field.getAnnotation(Min.class);
                    var minMaxAnnotation = field.getAnnotation(MinMax.class);
                    var min = -Float.MAX_VALUE;
                    var max = Float.MAX_VALUE;
                    if (minAnnotation != null)
                        min = minAnnotation.value();
                    if (minMaxAnnotation != null) {
                        min = minMaxAnnotation.min();
                        max = minMaxAnnotation.max();
                    }

                    var type = field.getType();
                    var name = fieldData.name();
                    var value = field.get(component);

                    switch (value) {
                        case Boolean v -> {
                            if (ImGui.checkbox(name, v))
                                field.set(component, !v);
                        }
                        case Integer v -> {
                            var out = ImGuiUtil.dragInt(name, v, min, max);
                            field.set(component, out);
                        }
                        case Float v -> {
                            var out = ImGuiUtil.dragFloat(name, v, min, max);
                            field.set(component, out);
                        }
                        case Double v -> {
                            var out = ImGuiUtil.dragFloat(name, v.floatValue(), min, max);
                            field.set(component, (double) out);
                        }
                        case String v -> {
                            var out = ImGuiUtil.inputText(name, v);
                            field.set(component, out);
                        }
                        case Vector2f v -> ImGuiUtil.dragFloat2(name, v);
                        case Vector3f v -> ImGuiUtil.dragFloat3(name, v);
                        case Vector4f v -> ImGuiUtil.dragFloat4(name, v, true);
                        case Quaternionf v -> ImGuiUtil.dragQuaternion(name, v);
                        case null, default -> {
                            if (type.isEnum()) {
                                // TODO: serialize enums
                            } else if (Resource.class.isAssignableFrom(type)) {
                                var r = (Resource) value;

                                //noinspection unchecked
                                var out = ImGuiUtil.inputResource(name, r, (Class<? extends Resource>) type);
                                field.set(component, out);
                            } else if (Component.class.isAssignableFrom(type)) {
                                var c = (Component) value;

                                //noinspection unchecked
                                drawComponentField(c, field, name, c, (Class<? extends Component>) type);
                            }
                        }
                    }
                }
                ImGui.unindent();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ImGui.popID();
    }

    private static <T extends Component> void drawComponentField(Component component, Field field, String
            name, Component value, Class<T> type) throws IllegalAccessException {
        if (value != null) {
            if (ImGui.button("-"))
                field.set(component, null);
            ImGui.sameLine();
        }
        ImGui.beginDisabled();
        var valueName = "";
        if (value != null)
            valueName = type.getSimpleName();
        ImGui.inputText(name, new ImString(valueName), ImGuiInputTextFlags.ReadOnly);
        ImGui.endDisabled();

        if (ImGui.beginDragDropTarget()) {
            var payload = (Component) ImGui.acceptDragDropPayload("component");

            if (payload != null && type.isAssignableFrom(payload.getClass()))
                field.set(component, payload);

            ImGui.endDragDropTarget();
        }

        if (ImGui.beginDragDropTarget()) {
            var payload = (GameObject) ImGui.acceptDragDropPayload("game_object");

            if (payload != null) {
                var c = payload.getComponent((Class<? extends Component>) type);
                if (c != null)
                    field.set(component, c);
            }

            ImGui.endDragDropTarget();
        }

        if (ImGui.isMouseDoubleClicked(0)) {
            if (value != null)
                component.getGameObject().getScene().setSelectedGameObject(value.getGameObject());
        }
    }

    private static boolean drawHeader(Component component) {
//        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 4);
        ImGui.pushStyleVar(ImGuiStyleVar.CellPadding, 2, 4);
        ImGui.beginTable("header", 3, ImGuiTableFlags.SizingFixedFit);

        ImGui.tableSetupColumn("##enable", ImGuiTableColumnFlags.NoHeaderWidth, 0);
        ImGui.tableSetupColumn("##name", ImGuiTableColumnFlags.WidthStretch, 0);
        ImGui.tableSetupColumn("##close", ImGuiTableColumnFlags.NoHeaderWidth, 0);

        ImGui.tableNextColumn();
        ImGui.checkbox("##enabled", true);

        ImGui.tableNextColumn();
        var open = ImGui.collapsingHeader(component.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen);

        if (ImGui.beginDragDropSource()) {
            ImGui.setDragDropPayload("component", component);
            ImGui.text(component.getClass().getSimpleName());
            ImGui.endDragDropSource();
        }

        ImGui.tableNextColumn();
        if (ImGui.button("X")) {
            component.getGameObject().removeComponent(component);
        }

        ImGui.endTable();
        ImGui.popStyleVar();

        return open;
    }
}
