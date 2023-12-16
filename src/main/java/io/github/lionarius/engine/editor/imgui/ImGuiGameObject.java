package io.github.lionarius.engine.editor.imgui;

import imgui.ImGui;
import imgui.ImGuiTextFilter;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.util.ReflectionUtil;
import lombok.experimental.UtilityClass;

import java.lang.reflect.InvocationTargetException;

@UtilityClass
public class ImGuiGameObject {
    public static void drawProperties(GameObject gameObject) {
        var newName = ImGuiUtil.inputText("Name", gameObject.getName());
        if (!newName.isEmpty())
            gameObject.setName(newName);

        var components = gameObject.getComponents();
        for (var component : components) {
            ImGui.separator();
            ImGuiComponent.drawProperties(component);
        }
        ImGui.separator();
        if (ImGui.button("Add component"))
            ImGui.openPopup("add_component");

        var filter = new ImGuiTextFilter();

        if (ImGui.beginPopup("add_component")) {
            filter.draw("##component_filter");
            ImGui.separator();

            var allComponents = ReflectionUtil.getAllComponentClasses();
            for (var component : allComponents) {
                var name = component.getSimpleName();
                if (filter.passFilter(name) && ImGui.selectable(name)) {
                    try {
                        var obj = (Component) component.getConstructor().newInstance();
                        gameObject.addComponent(obj);

                    } catch (NoSuchMethodException | InstantiationException | IllegalAccessException |
                             InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }
            }
            ImGui.endPopup();
        }
    }
}
