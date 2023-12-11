package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.renderer.text.TextRenderer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
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
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glEnable(GL46.GL_BLEND);
        GL46.glBlendFunc(GL46.GL_ONE, GL46.GL_ONE_MINUS_SRC_ALPHA);

        this.quadRenderer = new QuadRenderer(8192 * 4, this.resourceManager);
        this.quadRenderer.init();

        this.textRenderer = new TextRenderer(8192 * 16, this.resourceManager, this.resourceManager.get(Font.class, "font/roboto"));
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
