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
        ImGui.pushID(label);

        var out = new ImString(text, 256);

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        if (ImGui.inputText(label, out)) {
            ImGui.popID();
            return out.get();
        }
        ImGui.popID();

        return text;
    }

    public int dragInt(String label, int value, float min, float max) {
        ImGui.pushID(label);

        int[] out = {value};

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        ImGui.dragInt(label, out, getIntSpeed(value), min, max);

        ImGui.popID();

        return out[0];
    }

    private static float getIntSpeed(int value) {
        return Math.min((Math.abs(value / 100.0f) + 0.25f), 100.0f);
    }


    public float dragFloat(String label, float value, float min, float max) {
        ImGui.pushID(label);

        float[] out = {value};

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        ImGui.dragFloat(label, out, getFloatSpeed(value), min, max);

        ImGui.popID();

        return out[0];
    }

    private static float getFloatSpeed(float value) {
        return Math.min(((value * value / 10000.0f) + 0.005f), 100.0f);
    }

    public void dragFloat2(String label, Vector2f value) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y()};

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        // getFloatSpeed(Math.max(value.x(), value.y()))
        ImGui.dragFloat2(label, out);
        value.set(out);

        ImGui.popID();
    }

    public void dragFloat3(String label, Vector3f value) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y(), value.z()};

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        // getFloatSpeed(Math.max(Math.max(value.x(), value.y()), value.z()))
        ImGui.dragFloat3(label, out);
        value.set(out);

        ImGui.popID();
    }

    public void dragFloat4(String label, Vector4f value, boolean isColor) {
        ImGui.pushID(label);

        float[] out = {value.x(), value.y(), value.z(), value.w()};

//        ImGui.alignTextToFramePadding();
//        ImGui.text(label);
//        ImGui.sameLine();
        if (isColor)
            ImGui.colorEdit4(label, out);
        else
            ImGui.dragFloat4(label, out);
        value.set(out);

        ImGui.popID();
    }

    public static void dragQuaternion(String label, Quaternionf value) {
        var rotation = new Vector3f();
        value.getEulerAnglesXYZ(rotation).mul((float) (180.0f / Math.PI));
        var prevRotation = new Vector3f(rotation);

        ImGuiUtil.dragFloat3(label, rotation);

        rotation.sub(prevRotation).div((float) (180.0f / Math.PI));
        value.rotateY(rotation.y()).rotateZ(rotation.z()).rotateX(rotation.x());
    }

    public <T extends Resource> Resource inputResource(String label, Resource value, Class<T> clazz) {
        var name = "";
        if (value != null)
            name = value.getResourceName();
        var newName = ImGuiUtil.inputText(label, name);
        if (!Objects.equals(newName, name)) {
            var newResource = Logibuild.getInstance().getResourceManager().get(clazz, newName);
            if (newResource != null)
                return newResource;
        }

        return value;
    }
}
