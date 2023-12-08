package io.github.lionarius.engine.renderer.quad;

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
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class QuadRenderer implements Renderer {
    private static final int INDICES_PER_QUAD = 6;
    private static final int INSTANCE_SIZE = QuadVertexInstance.getLayout().getStride();

    private final int size;
    @NonNull
    private final ResourceManager resourceManager;
    private final QuadVertexInstance vertexInstance = new QuadVertexInstance();

    private int renderedCount;
    private Shader shader;

    private ByteBuffer buffer;
    private VertexBuffer commonVbo;
    private VertexBuffer instanceVbo;
    private IndexBuffer ibo;
    private VertexArray vao;

    @Override
    public void init() {
        this.shader = this.resourceManager.get(Shader.class, "shader/quad.shader");
        this.buffer = BufferUtils.createByteBuffer(QuadRenderer.INSTANCE_SIZE * this.size);

        this.ibo = new IndexBuffer(new int[]{0, 1, 2, 2, 3, 0});

        var vertices = new QuadVertexCommon[]{
                new QuadVertexCommon(new Vector3f(0, 0, 0), new Vector2f(0, 0)),
                new QuadVertexCommon(new Vector3f(1, 0, 0), new Vector2f(1, 0)),
                new QuadVertexCommon(new Vector3f(1, 1, 0), new Vector2f(1, 1)),
                new QuadVertexCommon(new Vector3f(0, 1, 0), new Vector2f(0, 0))
        };
        var commonData = BufferUtils.createByteBuffer(vertices.length * QuadVertexCommon.getLayout().getStride());
        BufferUtil.objectArrayToBuffer(vertices, commonData);
        commonData.position(0);
        this.commonVbo = new VertexBuffer(BufferUsage.STATIC_DRAW, commonData);

        this.instanceVbo = new VertexBuffer(BufferUsage.DYNAMIC_DRAW, QuadRenderer.INSTANCE_SIZE * this.size);

        this.vao = new VertexArray();
        this.vao.setIndexBuffer(this.ibo);
        this.vao.setVertexBuffer(0, 0, this.commonVbo, QuadVertexCommon.getLayout());
        this.vao.setVertexBuffer(1, 1, this.instanceVbo, QuadVertexInstance.getLayout());
    }

    @Override
    public void beginFrame() {
        this.renderedCount = 0;
        this.buffer.position(0);
    }

    public void renderQuad(Vector3fc position, Quaternionfc quaternion, Vector3fc size, Vector3fc scale, Vector4fc color) {
        var model = new Matrix4f().translate(position).rotate(quaternion).scale(scale).scale(size);

        this.renderQuad(model, color, -1);
    }

    public void renderQuad(Matrix4f model, Vector3fc size, Vector4fc color) {
        this.renderQuad(model.scale(size), color, -1);
    }

    private void renderQuad(Matrix4fc model, Vector4fc color, int textureId) {
        this.renderQuad(model, color.x(), color.y(), color.z(), color.w(), textureId);
    }

    private void renderQuad(Matrix4fc model, float r, float g, float b, float a, int textureId) {
        assert this.renderedCount < this.size : "exceeded batch size for quad renderer";

        this.vertexInstance.setModel(model);
        this.vertexInstance.setColor(r, g, b, a);
        this.vertexInstance.setTextureId(textureId);
        this.vertexInstance.get(this.buffer);

        this.renderedCount += 1;
    }

    @Override
    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        if (this.renderedCount <= 0)
            return;

        var bufferSlice = this.buffer.slice(0, QuadRenderer.INSTANCE_SIZE * this.renderedCount);
        this.instanceVbo.uploadData(bufferSlice);

        this.vao.bind();
        this.shader.bind();

        this.shader.setUniform("u_Projection", projection);
        this.shader.setUniform("u_View", view);

        GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, QuadRenderer.INDICES_PER_QUAD * this.renderedCount, GL46.GL_UNSIGNED_INT, 0, this.renderedCount);
    }

    @Override
    public void close() {
        this.vao.close();
        this.commonVbo.close();
        this.instanceVbo.close();
        this.ibo.close();
    }
}
