package io.github.lionarius.engine.editor.imgui;

import imgui.ImGui;
import imgui.flag.*;
import imgui.type.ImString;
import io.github.lionarius.engine.editor.CaseUtil;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.MinMax;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.util.ReflectionUtil;
import lombok.experimental.UtilityClass;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@UtilityClass
public class ImGuiComponent {
    public static void drawProperties(Component component) {
        ImGui.pushID(component.toString());
        try {
            if (ImGuiComponent.drawHeader(component)) {
                ImGui.indent();
                var fields = ReflectionUtil.getSerializableComponentFields(component.getClass());
                for (var field : fields) {
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

                    var isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate)
                        field.setAccessible(true);

                    var type = field.getType();
                    var name = CaseUtil.toSentenceCase(field.getName());
                    var value = field.get(component);

                    if (type.equals(boolean.class)) {
                        var val = (boolean) value;
                        if (ImGui.checkbox(name, val))
                            field.set(component, !val);
                    } else if (type.equals(int.class)) {
                        var out = ImGuiUtil.dragInt(name, (int) value, min, max);
                        field.set(component, out);
                    } else if (type.equals(float.class)) {
                        var out = ImGuiUtil.dragFloat(name, (float) value, min, max);
                        field.set(component, out);
                    } else if (type.equals(double.class)) {
                        var val = (Double) value;
                        var out = ImGuiUtil.dragFloat(name, val.floatValue(), min, max);
                        field.set(component, (double) out);
                    } else if (type.equals(Vector2f.class)) {
                        ImGuiUtil.dragFloat2(name, (Vector2f) value);
                    } else if (type.equals(Vector3f.class)) {
                        ImGuiUtil.dragFloat3(name, (Vector3f) value);
                    } else if (type.equals(Vector4f.class)) {
                        ImGuiUtil.dragFloat4(name, (Vector4f) value, true);
                    } else if (type.equals(String.class)) {
                        var out = ImGuiUtil.inputText(name, (String) value);
                        field.set(component, out);
                    } else if (Resource.class.isAssignableFrom(type)) {
                        var out = ImGuiUtil.inputResource(name, (Resource) value, (Class<? extends Resource>) type);
                        field.set(component, out);
                    } else if (Component.class.isAssignableFrom(type)) {
                        drawComponentField(component, field, name, (Component) value, type);
                    } else if (type.isEnum()) {

                    }

                    if (isPrivate)
                        field.setAccessible(false);
                }
                ImGui.unindent();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        ImGui.popID();
    }

    private static void drawComponentField(Component component, Field field, String name, Component value, Class<?> type) throws IllegalAccessException {
        ImGui.alignTextToFramePadding();
        ImGui.text(name);
        ImGui.sameLine();
        if (value != null) {
            if (ImGui.button("-"))
                field.set(component, null);
            ImGui.sameLine();
        }
        ImGui.beginDisabled();
        var valueName = "";
        if (value != null)
            valueName = type.getSimpleName();
        ImGui.inputText("##" + name, new ImString(valueName), ImGuiInputTextFlags.ReadOnly);
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
                component.getGameObject().getScene().setSelectedGameObject(((Component) value).getGameObject());
        }
    }

    private static boolean drawHeader(Component component) {
        ImGui.beginTable("##header", 3, ImGuiTableFlags.SizingFixedFit);
        ImGui.tableSetupColumn("##enable", ImGuiTableColumnFlags.NoHeaderWidth, 0);
        ImGui.tableSetupColumn("##name", ImGuiTableColumnFlags.WidthStretch, 0);
        ImGui.tableSetupColumn("##close", ImGuiTableColumnFlags.NoHeaderWidth, 0);

        ImGui.tableNextColumn();
        ImGui.pushStyleVar(ImGuiStyleVar.ItemSpacing, 0, 4);
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
        ImGui.popStyleVar();

        ImGui.endTable();
        return open;
    }
}
