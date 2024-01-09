package io.github.lionarius.engine.renderer.circle;

import io.github.lionarius.engine.renderer.RenderCamera;
import io.github.lionarius.engine.renderer.Renderer;
import io.github.lionarius.engine.renderer.buffer.BufferUsage;
import io.github.lionarius.engine.renderer.buffer.IndexBuffer;
import io.github.lionarius.engine.renderer.buffer.VertexArray;
import io.github.lionarius.engine.renderer.buffer.VertexBuffer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.shader.Shader;
import io.github.lionarius.engine.util.BufferUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Math;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class CircleRenderer extends Renderer {
    private static final int INDICES_PER_CIRCLE = 6;
    private static final int INSTANCE_SIZE = CircleVertexInstance.getLayout().getStride();

    private final int size;
    @NonNull
    private final ResourceManager resourceManager;
    private final CircleVertexInstance vertexInstance = new CircleVertexInstance();

    private int renderedCount;
    private Shader shader;

    private ByteBuffer buffer;
    private VertexBuffer commonVbo;
    private VertexBuffer instanceVbo;
    private IndexBuffer ibo;
    private VertexArray vao;

    @Override
    public void init() {
        this.shader = this.resourceManager.get(Shader.class, "shader/circle.shader");
        assert this.shader != null;

        this.buffer = BufferUtils.createByteBuffer(CircleRenderer.INSTANCE_SIZE * this.size);

        this.ibo = new IndexBuffer(new int[]{0, 1, 2});

        var vertices = new CircleVertexCommon[]{
                new CircleVertexCommon(new Vector3f(0, 1.0f, 0)),
                new CircleVertexCommon(new Vector3f(Math.sqrt(3) / 2.0f, -0.5f, 0)),
                new CircleVertexCommon(new Vector3f((-Math.sqrt(3) / 2.0f), -0.5f, 0)),
        };
        var commonData = BufferUtils.createByteBuffer(vertices.length * CircleVertexCommon.getLayout().getStride());
        BufferUtil.objectArrayToBuffer(vertices, commonData);
        commonData.position(0);
        this.commonVbo = new VertexBuffer(BufferUsage.STATIC_DRAW, commonData);

        this.instanceVbo = new VertexBuffer(BufferUsage.DYNAMIC_DRAW, CircleRenderer.INSTANCE_SIZE * this.size);

        this.vao = new VertexArray();
        this.vao.setIndexBuffer(this.ibo);
        this.vao.setVertexBuffer(0, 0, this.commonVbo, CircleVertexCommon.getLayout());
        this.vao.setVertexBuffer(1, 1, this.instanceVbo, CircleVertexInstance.getLayout());
    }

    @Override
    public void beginFrame(RenderCamera camera) {
        super.beginFrame(camera);

        this.renderedCount = 0;
        this.buffer.position(0);
    }

    public void renderCircle(Vector3fc position, Quaternionfc quaternion, Vector3fc size, Vector3fc scale, Vector4fc color) {
        var model = new Matrix4f().translate(position).rotate(quaternion).scale(scale).scale(size);

        this.renderCircle(model, color);
    }

    public void renderCircle(Matrix4f model, Vector3fc size, Vector4fc color) {
        this.renderCircle(model.scale(size), color);
    }

    private void renderCircle(Matrix4fc model, Vector4fc color) {
        this.renderCircle(model, color.x(), color.y(), color.z(), color.w());
    }

    private void renderCircle(Matrix4fc model, float r, float g, float b, float a) {
        assert this.renderedCount < this.size : "exceeded batch size for circle renderer";

        this.vertexInstance.setModel(model);
        this.vertexInstance.setColor(r, g, b, a);
        this.vertexInstance.get(this.buffer);

        this.renderedCount += 1;
    }

    @Override
    public void endFrame() {
        if (this.renderedCount <= 0)
            return;

        var bufferSlice = this.buffer.slice(0, CircleRenderer.INSTANCE_SIZE * this.renderedCount);
        this.instanceVbo.uploadData(bufferSlice);

        this.vao.bind();
        this.shader.bind();

        this.shader.setUniform("u_Projection", this.camera.getProjection());
        this.shader.setUniform("u_View", this.camera.getView());

        GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, CircleRenderer.INDICES_PER_CIRCLE * this.renderedCount, GL46.GL_UNSIGNED_INT, 0, this.renderedCount);
    }

    @Override
    public void close() {
        this.vao.close();
        this.commonVbo.close();
        this.instanceVbo.close();
        this.ibo.close();
    }
}
