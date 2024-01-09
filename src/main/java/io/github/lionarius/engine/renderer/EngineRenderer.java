package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.circle.CircleRenderer;
import io.github.lionarius.engine.renderer.framebuffer.Framebuffer;
import io.github.lionarius.engine.renderer.line.LineRenderer;
import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.renderer.text.TextRenderer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.lwjgl.opengl.GL46;

@RequiredArgsConstructor
public class EngineRenderer extends Renderer {
    @NonNull
    private final ResourceManager resourceManager;
    @Getter
    private Framebuffer framebuffer;

    @Getter
    private QuadRenderer quadRenderer;
    @Getter
    private CircleRenderer circleRenderer;
    @Getter
    private TextRenderer textRenderer;
    @Getter
    private LineRenderer lineRenderer;


    @Override
    public void init() {
        GL46.glEnable(GL46.GL_DEPTH_TEST);
        GL46.glBlendFunc(GL46.GL_SRC_ALPHA, GL46.GL_ONE_MINUS_SRC_ALPHA);
        GL46.glEnable(GL46.GL_BLEND);

        GL46.glEnable(GL46.GL_DEBUG_OUTPUT);
        GL46.glEnable(GL46.GL_DEBUG_OUTPUT_SYNCHRONOUS);

        GL46.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);

        this.quadRenderer = new QuadRenderer(8192 * 4, this.resourceManager);
        this.quadRenderer.init();

        this.circleRenderer = new CircleRenderer(8192, this.resourceManager);
        this.circleRenderer.init();

        this.textRenderer = new TextRenderer(8192 * 16, this.resourceManager, this.resourceManager.get(Font.class, "font/atlas/roboto"));
        this.textRenderer.init();

        this.lineRenderer = new LineRenderer(8192 * 16, this.resourceManager);
        this.lineRenderer.init();

        this.framebuffer = new Framebuffer(1, 1);
        this.framebuffer.init();
    }

    @Override
    public void beginFrame(RenderCamera camera) {
        this.quadRenderer.beginFrame(camera);
        this.circleRenderer.beginFrame(camera);
        this.textRenderer.beginFrame(camera);
        this.lineRenderer.beginFrame(camera);

        this.framebuffer.bind();
    }

    public void beginScreenFrame(RenderCamera camera) {
        this.quadRenderer.beginFrame(camera);
        this.circleRenderer.beginFrame(camera);
        this.textRenderer.beginFrame(camera);
        this.lineRenderer.beginFrame(camera);
    }

    public void endScreenFrame() {
        this.clear();

        this.quadRenderer.endFrame();
        this.circleRenderer.endFrame();
        this.textRenderer.endFrame();
        this.lineRenderer.endFrame();
    }

    public void endEmptyFrame() {
        this.clear();
        this.framebuffer.unbind();
    }

    @Override
    public void endFrame() {
        this.endScreenFrame();

        this.framebuffer.unbind();
    }

    public void clear() {
        GL46.glClear(GL46.GL_COLOR_BUFFER_BIT | GL46.GL_DEPTH_BUFFER_BIT);
    }

    @Override
    public void close() {
        this.framebuffer.close();

        this.quadRenderer.close();
        this.circleRenderer.close();
        this.textRenderer.close();
        this.lineRenderer.close();
    }
}
