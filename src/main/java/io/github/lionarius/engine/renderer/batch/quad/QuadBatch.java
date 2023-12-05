package io.github.lionarius.engine.renderer.batch.quad;

import io.github.lionarius.engine.renderer.batch.RenderBatch;
import io.github.lionarius.engine.renderer.buffer.BufferUsage;
import io.github.lionarius.engine.renderer.buffer.IndexBuffer;
import io.github.lionarius.engine.renderer.buffer.VertexArray;
import io.github.lionarius.engine.renderer.buffer.VertexBuffer;
import io.github.lionarius.engine.renderer.shader.Shader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.util.BufferUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class QuadBatch implements RenderBatch {
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

    private static int[] generateIndices(int size) {
        var count = QuadBatch.INDICES_PER_QUAD * size;
        var indices = new int[count];
        var vertex = 0;
        for (int i = 0; i < count; i += 6) {
            indices[i + 0] = 0 + vertex;
            indices[i + 1] = 1 + vertex;
            indices[i + 2] = 2 + vertex;
            indices[i + 3] = 2 + vertex;
            indices[i + 4] = 3 + vertex;
            indices[i + 5] = 0 + vertex;

            vertex += 4;
        }

        return indices;
    }

    @Override
    public void init() {
        this.shader = this.resourceManager.get(Shader.class, "shader/quad.shader");
        this.buffer = BufferUtils.createByteBuffer(QuadBatch.INSTANCE_SIZE * this.size);

        this.ibo = new IndexBuffer(QuadBatch.generateIndices(1));

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

        this.instanceVbo = new VertexBuffer(BufferUsage.STREAM_DRAW, QuadBatch.INSTANCE_SIZE * this.size);

        this.vao = new VertexArray();
        this.vao.addBuffer(0, 0, this.commonVbo, QuadVertexCommon.getLayout());
        this.vao.addBuffer(1, 1, this.instanceVbo, QuadVertexInstance.getLayout());
    }

    @Override
    public void beginFrame() {
        this.renderedCount = 0;
        this.buffer.position(0);
    }

    public void renderQuad(float x, float y, float z, float angle, Vector3fc axis, float width, float height, Vector4fc color) {
        assert this.renderedCount < this.size : "exceeded batch size for quad batch";

        var model = new Matrix4f().translate(x, y, z).scale(width, height, 0).rotate(angle, axis);

        this.vertexInstance.setModel(model);
        this.vertexInstance.setColor(color);
        this.vertexInstance.setTextureId(-1);
        this.vertexInstance.get(this.buffer);

        this.renderedCount += 1;
    }

    @Override
    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        if (this.renderedCount <= 0)
            return;

        this.ibo.bind();
        this.vao.bind();
        this.shader.bind();
        var bufferSlice = this.buffer.slice(0, QuadBatch.INSTANCE_SIZE * this.renderedCount);
        this.instanceVbo.uploadData(bufferSlice);

        this.shader.setUniform("u_Projection", projection);
        this.shader.setUniform("u_View", view);

        GL46.glDrawElementsInstanced(GL46.GL_TRIANGLES, QuadBatch.INDICES_PER_QUAD, GL46.GL_UNSIGNED_INT, 0, this.renderedCount);
    }

    @Override
    public void close() {
        this.vao.close();
        this.commonVbo.close();
        this.instanceVbo.close();
        this.ibo.close();
    }
}
