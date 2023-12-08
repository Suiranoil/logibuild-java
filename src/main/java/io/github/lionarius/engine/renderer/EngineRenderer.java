package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.renderer.text.TextRenderer;
import io.github.lionarius.engine.resource.ResourceManager;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4fc;
import org.lwjgl.opengl.GL46;

@RequiredArgsConstructor
public class EngineRenderer implements Renderer {
    @NonNull
    private final ResourceManager resourceManager;

    @Getter
    private QuadRenderer quadRenderer;
    @Getter
    private TextRenderer textRenderer;


    public void init() {
//        GL46.gl

        this.quadRenderer = new QuadRenderer(8192 * 4, this.resourceManager);
        this.quadRenderer.init();

        this.textRenderer = new TextRenderer(8192 * 4, this.resourceManager);
        this.textRenderer.init();
    }

    public void beginFrame() {
        this.quadRenderer.beginFrame();
        this.textRenderer.beginFrame();
    }

    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);

        this.quadRenderer.endFrame(projection, view);
        this.textRenderer.endFrame(projection, view);
    }

    @Override
    public void close() {
        this.quadRenderer.close();
    }
}
