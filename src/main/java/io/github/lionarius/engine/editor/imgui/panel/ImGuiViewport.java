package io.github.lionarius.engine.editor.imgui.panel;

import imgui.ImGui;
import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.editor.imgui.ImGuiUtil;
import io.github.lionarius.engine.renderer.EngineRenderer;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.resource.impl.texture.TextureCreateParameters;
import io.github.lionarius.engine.scene.SceneManager;
import io.github.lionarius.engine.util.io.JsonUtil;
import org.joml.Vector2i;
import org.lwjgl.opengl.GL46;

import java.io.File;

public class ImGuiViewport {
    private static final Texture PLAY_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/play.png", TextureCreateParameters.SMOOTH);
    private static final Texture STOP_ICON = Logibuild.getInstance().getInternalResourceManager().get(Texture.class, "icons/stop.png", TextureCreateParameters.SMOOTH);

    private final SceneManager sceneManager = Logibuild.getInstance().getSceneManager();
    private final EngineRenderer engineRenderer = Logibuild.getInstance().getEngineRenderer();

    public void drawViewport() {
        var playing = this.sceneManager.isPlaying();
        var icon = playing ? ImGuiViewport.STOP_ICON : ImGuiViewport.PLAY_ICON;
        ImGuiUtil.prepareButtonAligned(20, 0.5f);
        if (ImGui.imageButton(icon.getId(), 20, 20, 0, 1, 1, 0)) {
            if (playing)
                this.sceneManager.stopPlaying();
            else
                this.sceneManager.startPlaying();

        }
        var viewportSize = ImGui.getContentRegionAvail();
        GL46.glViewport(0, 0, (int) viewportSize.x, (int) viewportSize.y);
        var camera = this.sceneManager.getSceneCamera();
        var framebuffer = this.engineRenderer.getFramebuffer();
        if (camera != null) {
            camera.setFrameSize(new Vector2i((int) viewportSize.x, (int) viewportSize.y));
            framebuffer.resize((int) viewportSize.x, (int) viewportSize.y);
            ImGui.image(framebuffer.getTexture().getId(), framebuffer.getWidth(), framebuffer.getHeight(), 0, 1, 1, 0);
        }

        if (this.sceneManager.isEditor()) {
            if (ImGui.beginDragDropTarget()) {
                var payload = (File) ImGui.acceptDragDropPayload("explorer_file");
                if (payload != null) {
                    if (payload.getName().endsWith(".scene")) {
                        var scene = JsonUtil.loadSceneFromFile(payload.getAbsolutePath());
                        if (scene != null)
                            this.sceneManager.transitionTo(scene);
                    }
                }
            }
        }
    }
}
