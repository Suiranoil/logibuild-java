package io.github.lionarius.engine.editor;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.util.Closeable;
import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;

@RequiredArgsConstructor
public class ImGuiLayer implements Closeable {
    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private final Window window;

    public void init() {
        ImGui.createContext();

        this.configure();

        this.imGuiImplGlfw.init(this.window.getHandle(), true);
        this.imGuiImplGl3.init("#version 460 core");
    }

    private void configure() {
        final var io = ImGui.getIO();

        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
    }

    public void beginFrame() {
        this.imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void endFrame() {
        ImGui.render();

        if (ImGui.getIO().hasConfigFlags(ImGuiConfigFlags.ViewportsEnable)) {
            final long backupWindowPtr = GLFW.glfwGetCurrentContext();
            ImGui.updatePlatformWindows();
            ImGui.renderPlatformWindowsDefault();
            GLFW.glfwMakeContextCurrent(backupWindowPtr);
        }
    }

    public void render() {
        this.imGuiImplGl3.renderDrawData(ImGui.getDrawData());
    }

    @Override
    public void close() {
        this.imGuiImplGl3.dispose();
        this.imGuiImplGlfw.dispose();
        ImGui.destroyContext();
    }
}
