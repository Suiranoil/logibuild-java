package io.github.lionarius.engine.renderer.line;

import io.github.lionarius.engine.renderer.RenderCamera;
import io.github.lionarius.engine.renderer.Renderer;
import io.github.lionarius.engine.renderer.buffer.BufferUsage;
import io.github.lionarius.engine.renderer.buffer.VertexArray;
import io.github.lionarius.engine.renderer.buffer.VertexBuffer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.shader.Shader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3fc;
import org.joml.Vector4fc;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class LineRenderer extends Renderer {
    private static final int ELEMENT_SIZE = LineVertex.getLayout().getStride();

    private final int size;
    @NonNull
    private final ResourceManager resourceManager;
    private final LineVertex lineVertex = new LineVertex();

    private int renderedCount;
    private Shader shader;

    private ByteBuffer buffer;
    private VertexBuffer vbo;
    private VertexArray vao;

    @Override
    public void init() {
        this.shader = this.resourceManager.get(Shader.class, "shader/line.shader");
        assert this.shader != null;

        this.buffer = BufferUtils.createByteBuffer(LineRenderer.ELEMENT_SIZE * this.size);

        this.vbo = new VertexBuffer(BufferUsage.DYNAMIC_DRAW, LineRenderer.ELEMENT_SIZE * this.size);
        this.vao = new VertexArray();
        this.vao.setVertexBuffer(0, 0, this.vbo, LineVertex.getLayout());
    }

    @Override
    public void beginFrame(RenderCamera camera) {
        super.beginFrame(camera);

        this.renderedCount = 0;
        this.buffer.position(0);
    }

    public void renderLine(Vector3fc a, Vector3fc b, Vector4fc color) {
        assert this.renderedCount < this.size : "exceeded batch size for text renderer";

        this.lineVertex.setColor(color.x(), color.y(), color.z(), color.w());

        this.lineVertex.setPosition(a.x(), a.y(), a.z());
        this.lineVertex.get(this.buffer);

        this.lineVertex.setPosition(b.x(), b.y(), b.z());
        this.lineVertex.get(this.buffer);

        this.renderedCount += 2;
    }

    @Override
    public void endFrame() {
        if (this.renderedCount <= 0)
            return;

        var bufferSlice = this.buffer.slice(0, LineRenderer.ELEMENT_SIZE * this.renderedCount);
        this.vbo.uploadData(bufferSlice);

        this.vao.bind();
        this.shader.bind();

        this.shader.setUniform("u_Projection", this.camera.getProjection());
        this.shader.setUniform("u_View", this.camera.getView());

        GL46.glDisable(GL46.GL_DEPTH_TEST);
        GL46.glDrawArrays(GL46.GL_LINES, 0, this.renderedCount);
        GL46.glEnable(GL46.GL_DEPTH_TEST);
    }

    @Override
    public void close() {
        this.vao.close();
        this.vbo.close();
    }
}
