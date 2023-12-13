package io.github.lionarius.engine.editor;

import imgui.ImGui;
import imgui.flag.*;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import imgui.type.ImInt;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.util.Closeable;
import lombok.RequiredArgsConstructor;
import org.lwjgl.glfw.GLFW;

@RequiredArgsConstructor
public class ImGuiLayer implements Closeable {
    private final ImGuiImplGlfw imGuiImplGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiImplGl3 = new ImGuiImplGl3();
    private final Window window;

    private boolean isFirstFrame;

    public void init() {
        ImGui.createContext();

        this.configure();
        this.setupStyle();
        this.isFirstFrame = true;

        this.imGuiImplGlfw.init(this.window.getHandle(), true);
        this.imGuiImplGl3.init("#version 460 core");
    }

    private void configure() {
        final var io = ImGui.getIO();

        io.setIniFilename(null);
        io.addConfigFlags(ImGuiConfigFlags.ViewportsEnable);
        io.addConfigFlags(ImGuiConfigFlags.DockingEnable);

        io.getFonts().addFontFromFileTTF(ClassLoader.getSystemResource("assets/font/Roboto-Medium.ttf").getFile().substring(1), 16);
        io.getFonts().build();
    }

    public void beginFrame() {
        this.imGuiImplGlfw.newFrame();
        ImGui.newFrame();
    }

    public void begin() {
        var viewport = ImGui.getMainViewport();
        ImGui.setNextWindowPos(viewport.getPosX(), viewport.getPosY());
        ImGui.setNextWindowSize(viewport.getSizeX(), viewport.getSizeY());
        ImGui.setNextWindowViewport(viewport.getID());

        ImGui.pushStyleVar(ImGuiStyleVar.WindowBorderSize, 0.0f);
        ImGui.pushStyleVar(ImGuiStyleVar.WindowPadding, 0, 0);
        ImGui.begin("DockSpace",
                ImGuiWindowFlags.NoDocking | ImGuiWindowFlags.NoResize |
                ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoTitleBar |
                ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoBringToFrontOnFocus |
                ImGuiWindowFlags.NoNavFocus
        );
        ImGui.popStyleVar(2);

        var dockSpaceId = new ImInt(ImGui.getID("MainDockSpace"));
        ImGui.dockSpace(dockSpaceId.get(), 0, 0);

        if (this.isFirstFrame) {
            this.isFirstFrame = false;

            imgui.internal.ImGui.dockBuilderRemoveNode(dockSpaceId.get());
            imgui.internal.ImGui.dockBuilderAddNode(dockSpaceId.get());
            imgui.internal.ImGui.dockBuilderSetNodeSize(dockSpaceId.get(), viewport.getSizeX(), viewport.getSizeY());

            var rightId = imgui.internal.ImGui.dockBuilderSplitNode(dockSpaceId.get(), ImGuiDir.Right, 0.2f, null, dockSpaceId);
            var leftId = imgui.internal.ImGui.dockBuilderSplitNode(dockSpaceId.get(), ImGuiDir.Left, 0.2f, null, dockSpaceId);

            imgui.internal.ImGui.dockBuilderDockWindow("Scene", dockSpaceId.get());
            imgui.internal.ImGui.dockBuilderDockWindow("Properties", rightId);
            imgui.internal.ImGui.dockBuilderDockWindow("Hierarchy", leftId);

            imgui.internal.ImGui.dockBuilderFinish(dockSpaceId.get());
        }

        ImGui.end();
    }

    public void end() {
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

    private void setupStyle() {
        final var style = ImGui.getStyle();

        style.setWindowPadding(8.00f, 8.00f);
        style.setFramePadding(5.00f, 2.00f);
        style.setCellPadding(6.00f, 6.00f);
        style.setItemSpacing(6.00f, 6.00f);
        style.setItemInnerSpacing(6.00f, 6.00f);
        style.setTouchExtraPadding(0.00f, 0.00f);
        style.setIndentSpacing(25);
        style.setScrollbarSize(15);
        style.setGrabMinSize(10);
        style.setWindowBorderSize(1);
        style.setChildBorderSize(1);
        style.setPopupBorderSize(1);
        style.setFrameBorderSize(1);
        style.setTabBorderSize(1);
        style.setWindowRounding(7);
        style.setChildRounding(4);
        style.setFrameRounding(3);
        style.setPopupRounding(4);
        style.setScrollbarRounding(9);
        style.setGrabRounding(3);
        style.setLogSliderDeadzone(4);
        style.setTabRounding(4);

        style.setColor(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.10f, 0.10f, 0.10f, 1.00f);
        style.setColor(ImGuiCol.ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.PopupBg, 0.19f, 0.19f, 0.19f, 0.92f);
        style.setColor(ImGuiCol.Border, 0.39f, 0.39f, 0.39f, 0.29f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.24f);
        style.setColor(ImGuiCol.FrameBg, 0.05f, 0.05f, 0.05f, 0.54f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.19f, 0.19f, 0.19f, 0.54f);
        style.setColor(ImGuiCol.FrameBgActive, 0.20f, 0.22f, 0.23f, 1.00f);
        style.setColor(ImGuiCol.TitleBg, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.06f, 0.06f, 0.06f, 1.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 1.00f);
        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.05f, 0.05f, 0.05f, 0.54f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.34f, 0.34f, 0.34f, 0.54f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.40f, 0.40f, 0.40f, 0.54f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.56f, 0.56f, 0.56f, 0.54f);
        style.setColor(ImGuiCol.CheckMark, 0.33f, 0.67f, 0.86f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.34f, 0.34f, 0.34f, 0.54f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.56f, 0.56f, 0.56f, 0.54f);
        style.setColor(ImGuiCol.Button, 0.05f, 0.05f, 0.05f, 0.54f);
        style.setColor(ImGuiCol.ButtonHovered, 0.19f, 0.19f, 0.19f, 0.54f);
        style.setColor(ImGuiCol.ButtonActive, 0.20f, 0.22f, 0.23f, 1.00f);
        style.setColor(ImGuiCol.Header, 0.00f, 0.00f, 0.00f, 0.52f);
        style.setColor(ImGuiCol.HeaderHovered, 0.00f, 0.00f, 0.00f, 0.36f);
        style.setColor(ImGuiCol.HeaderActive, 0.20f, 0.22f, 0.23f, 0.33f);
        style.setColor(ImGuiCol.Separator, 0.28f, 0.28f, 0.28f, 0.29f);
        style.setColor(ImGuiCol.SeparatorHovered, 0.44f, 0.44f, 0.44f, 0.29f);
        style.setColor(ImGuiCol.SeparatorActive, 0.40f, 0.44f, 0.47f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 0.28f, 0.28f, 0.28f, 0.29f);
        style.setColor(ImGuiCol.ResizeGripHovered, 0.44f, 0.44f, 0.44f, 0.29f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.40f, 0.44f, 0.47f, 1.00f);
        style.setColor(ImGuiCol.Tab, 0.00f, 0.00f, 0.00f, 0.52f);
        style.setColor(ImGuiCol.TabHovered, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.TabActive, 0.20f, 0.20f, 0.20f, 0.36f);
        style.setColor(ImGuiCol.TabUnfocused, 0.00f, 0.00f, 0.00f, 0.52f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.DockingPreview, 0.35f, 0.35f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.00f, 0.00f, 0.00f, 0.52f);
        style.setColor(ImGuiCol.TableBorderStrong, 0.00f, 0.00f, 0.00f, 0.52f);
        style.setColor(ImGuiCol.TableBorderLight, 0.28f, 0.28f, 0.28f, 0.29f);
        style.setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt, 1.00f, 1.00f, 1.00f, 0.06f);
        style.setColor(ImGuiCol.TextSelectedBg, 0.20f, 0.22f, 0.23f, 1.00f);
        style.setColor(ImGuiCol.DragDropTarget, 0.33f, 0.67f, 0.86f, 1.00f);
    }
}
