package io.github.lionarius.engine.scene;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import io.github.lionarius.engine.editor.ImGuiUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Modifier;

@NoArgsConstructor
public abstract class Component {
    @Getter @Setter(AccessLevel.PROTECTED)
    private GameObject gameObject;

    public void onAwake() {
    }

    public void onStart() {
    }

    public void onUpdate(double delta) {
    }

    public void onRender(double delta) {
    }

    public void onDestroy() {
    }

    public final void imgui() {
        ImGui.pushID(this.toString());
        try {
            if (ImGui.collapsingHeader(this.getClass().getSimpleName(), ImGuiTreeNodeFlags.DefaultOpen)) {
                ImGui.indent();
                var fields = this.getClass().getDeclaredFields();
                for (var field : fields) {
                    var isTransient = Modifier.isTransient(field.getModifiers());
                    if (isTransient)
                        continue;

                    var isPrivate = Modifier.isPrivate(field.getModifiers());
                    if (isPrivate)
                        field.setAccessible(true);

                    var type = field.getType();
                    var name = field.getName();
                    var value = field.get(this);

                    if (type.equals(boolean.class)) {
                        var val = (boolean) value;
                        if (ImGui.checkbox(name, val))
                            field.set(this, !val);
                    } else if (type.equals(int.class)) {
                        var out = ImGuiUtil.dragInt(name, (int) value);
                        field.set(this, out);
                    } else if (type.equals(float.class)) {
                        var out = ImGuiUtil.dragFloat(name, (float) value);
                        field.set(this, out);
                    } else if (type.equals(Vector2f.class)) {
                        ImGuiUtil.drawVec2(name, (Vector2f) value);
                    } else if (type.equals(Vector3f.class)) {
                        ImGuiUtil.drawVec3(name, (Vector3f) value);
                    } else if (type.equals(Vector4f.class)) {

                    } else if (type.equals(String.class)) {
                        var out = ImGuiUtil.inputText(name, (String) value);
                        field.set(this, out);
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
}
