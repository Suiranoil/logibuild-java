package io.github.lionarius.engine.editor;

import imgui.flag.ImGuiTableFlags;
import imgui.internal.ImGui;
import imgui.type.ImString;
import lombok.experimental.UtilityClass;
import org.joml.Vector2f;
import org.joml.Vector3f;

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

    public void drawVec2(String label, Vector2f value) {
        ImGui.pushID(label);

        ImGui.alignTextToFramePadding();
        ImGui.text(label);
        ImGui.sameLine();
        if (ImGui.beginTable("##table", 2, ImGuiTableFlags.SizingStretchProp)) {
            ImGui.tableNextRow();

            ImGui.tableNextColumn();
            float[] x = {value.x()};
            ImGui.dragFloat("##x" + label, x);

            ImGui.tableNextColumn();
            float[] y = {value.y()};
            ImGui.dragFloat("##y" + label, y);

            value.set(x[0], y[0]);

            ImGui.endTable();
        }

        ImGui.popID();
    }

    public void drawVec3(String label, Vector3f value) {
        ImGui.pushID(label);

        ImGui.text(label);
        if (ImGui.beginTable("##table", 3, ImGuiTableFlags.SizingStretchProp)) {
            ImGui.tableNextRow();

            ImGui.tableNextColumn();
            float[] x = {value.x()};
            ImGui.alignTextToFramePadding();
            ImGui.text("X");
            ImGui.sameLine();
            ImGui.dragFloat("##x" + label, x);

            ImGui.tableNextColumn();
            float[] y = {value.y()};
            ImGui.alignTextToFramePadding();
            ImGui.text("Y");
            ImGui.sameLine();
            ImGui.dragFloat("##y" + label, y);

            ImGui.tableNextColumn();
            float[] z = {value.z()};
            ImGui.alignTextToFramePadding();
            ImGui.text("Z");
            ImGui.sameLine();
            ImGui.dragFloat("##z" + label, z);

            value.set(x[0], y[0], z[0]);

            ImGui.endTable();
        }

        ImGui.popID();
    }
}
