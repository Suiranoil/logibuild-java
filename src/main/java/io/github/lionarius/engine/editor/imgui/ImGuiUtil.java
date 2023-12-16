package io.github.lionarius.engine.editor.imgui;

import imgui.internal.ImGui;
import imgui.type.ImString;
import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.resource.Resource;
import lombok.experimental.UtilityClass;
import org.joml.Math;
import org.joml.*;

import java.util.Objects;

@UtilityClass
public class ImGuiUtil {

    public String inputText(String label, String text) {
        var out = new ImString(text, 256);

        if (ImGui.inputText(label, out))
            return out.get();

        return text;
    }

    public int dragInt(String label, int value, float min, float max) {
        int[] out = {value};

        ImGui.dragInt(label, out, getIntSpeed(value), min, max);

        return out[0];
    }

    public void dragInt2(String label, Vector2i value) {
        int[] out = {value.x(), value.y()};

        ImGui.dragInt2(label, out);
        value.set(out);
    }

    public void dragInt3(String label, Vector3i value) {
        int[] out = {value.x(), value.y(), value.z()};

        ImGui.dragInt3(label, out);
        value.set(out);
    }

    private static float getIntSpeed(int value) {
        return Math.min((Math.abs(value / 100.0f) + 0.25f), 100.0f);
    }


    public float dragFloat(String label, float value, float min, float max) {
        float[] out = {value};

        ImGui.dragFloat(label, out, getFloatSpeed(value), min, max);

        return out[0];
    }

    private static float getFloatSpeed(float value) {
        return Math.min(((value * value / 10000.0f) + 0.005f), 100.0f);
    }

    public void dragFloat2(String label, Vector2f value) {
        float[] out = {value.x(), value.y()};

        ImGui.dragFloat2(label, out);
        value.set(out);
    }

    public void dragFloat3(String label, Vector3f value) {
        float[] out = {value.x(), value.y(), value.z()};

        ImGui.dragFloat3(label, out);
        value.set(out);
    }

    public void dragFloat4(String label, Vector4f value, boolean isColor) {
        float[] out = {value.x(), value.y(), value.z(), value.w()};

        if (isColor)
            ImGui.colorEdit4(label, out);
        else
            ImGui.dragFloat4(label, out);
        value.set(out);
    }

    public static void dragQuaternion(String label, Quaternionf value) {
        var rotation = new Vector3f();
        value.getEulerAnglesXYZ(rotation).mul((float) (180.0f / Math.PI));
        var prevRotation = new Vector3f(rotation);

        ImGuiUtil.dragFloat3(label, rotation);

        rotation.sub(prevRotation).div((float) (180.0f / Math.PI));
        value.rotateY(rotation.y()).rotateZ(rotation.z()).rotateX(rotation.x());
    }

    public static <T extends Resource> Resource inputResource(String label, Resource value, Class<T> clazz) {
        var name = "";
        if (value != null)
            name = value.getResourceName();
        var newName = ImGuiUtil.inputText(label, name);
        if (!Objects.equals(newName, name)) {
            var newResource = Logibuild.getInstance().getResourceManager().get(clazz, newName);
            if (newResource != null || newName.isEmpty())
                return newResource;
        }

        return value;
    }

    public static boolean buttonAligned(String label, float alignment) {
        var style = ImGui.getStyle();

        float size = ImGui.calcTextSize(label).x + style.getFramePadding().x * 2.0f;
        float avail = ImGui.getContentRegionAvail().x;

        float off = (avail - size) * alignment;
        if (off > 0.0f)
            ImGui.setCursorPosX(ImGui.getCursorPosX() + off);

        return ImGui.button(label);
    }
}
