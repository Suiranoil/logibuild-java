package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.quad.QuadRenderer;
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


    public void init() {
        this.quadRenderer = new QuadRenderer(8192 * 4, this.resourceManager);
        this.quadRenderer.init();
    }

    public void beginFrame() {
        this.quadRenderer.beginFrame();
    }

    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);

        this.quadRenderer.endFrame(projection, view);
    }

    @Override
    public void close() {
        this.quadRenderer.close();
    }
}
