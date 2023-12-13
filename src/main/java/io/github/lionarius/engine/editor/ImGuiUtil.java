package io.github.lionarius.engine.editor;

import imgui.internal.ImGui;
import imgui.type.ImString;
import lombok.experimental.UtilityClass;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

@UtilityClass
public class ImGuiUtil {

    public String inputText(String label, String text) {
        ImGui.pushID(label);

        var out = new ImString(text, 256);

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        if (ImGui.inputText("##" + label, out)) {
            ImGui.popID();
            return out.get();
        }
        ImGui.popID();

        return text;
    }

    public int dragInt(String label, int value) {
        ImGui.pushID(label);

        int[] out = {value};

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        ImGui.dragInt("##" + label, out);

        ImGui.popID();

        return out[0];
    }

    public float dragFloat(String label, float value) {
        ImGui.pushID(label);

        float[] out = {value};

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        ImGui.dragFloat("##" + label, out);

        ImGui.popID();

        return out[0];
    }

    public void dragFloat2(String label, Vector2f value) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y()};

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        ImGui.dragFloat2("##" + label, out);
        value.set(out);

        ImGui.popID();
    }

    public void dragFloat3(String label, Vector3f value) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y(), value.z()};

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        ImGui.dragFloat3("##" + label, out);
        value.set(out);

        ImGui.popID();
    }

    public void dragFloat4(String label, Vector4f value, boolean isColor) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y(), value.z(), value.w()};

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        if (isColor)
            ImGui.colorEdit4("##" + label, out);
        else
            ImGui.dragFloat4("##" + label, out);
        value.set(out);

        ImGui.popID();
    }
}
