package io.github.lionarius.engine.renderer.text;

import io.github.lionarius.engine.renderer.Renderer;
import io.github.lionarius.engine.renderer.buffer.BufferUsage;
import io.github.lionarius.engine.renderer.buffer.IndexBuffer;
import io.github.lionarius.engine.renderer.buffer.VertexArray;
import io.github.lionarius.engine.renderer.buffer.VertexBuffer;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.resource.font.TextGlyphIterator;
import io.github.lionarius.engine.resource.shader.Shader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.*;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class TextRenderer implements Renderer {
    private static final int INDICES_PER_CHAR = 6;
    private static final int VERTICES_PER_CHAR = 4;
    private static final int VERTEX_SIZE = TextVertex.getLayout().getStride();

    private final int size;
    @NonNull
    private final ResourceManager resourceManager;
    private final TextVertex textVertex = new TextVertex();

    private int renderedCharsCount;
    private Shader shader;
    private Font defaultFont;

    private ByteBuffer buffer;
    private VertexBuffer vbo;
    private IndexBuffer ibo;
    private VertexArray vao;

    private static int[] generateIndices(int size) {
        var count = TextRenderer.INDICES_PER_CHAR * size;
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
        this.shader = this.resourceManager.get(Shader.class, "shader/text.shader");
        this.defaultFont = this.resourceManager.get(Font.class, "font/minecraft");

        this.buffer = BufferUtils.createByteBuffer(TextRenderer.VERTEX_SIZE * TextRenderer.VERTICES_PER_CHAR * this.size);

        this.ibo = new IndexBuffer(TextRenderer.generateIndices(this.size));
        this.vbo = new VertexBuffer(BufferUsage.DYNAMIC_DRAW, TextRenderer.VERTEX_SIZE * TextRenderer.VERTICES_PER_CHAR * this.size);

        this.vao = new VertexArray();
        this.vao.setIndexBuffer(this.ibo);
        this.vao.setVertexBuffer(this.vbo, TextVertex.getLayout());
    }

    @Override
    public void beginFrame() {
        this.renderedCharsCount = 0;
        this.buffer.position(0);
    }

    public void renderText(String text, Vector3fc position, Quaternionfc quaternion, float height, Vector4fc color) {
        var model = new Matrix4f().translate(position).rotate(quaternion).scale(height / this.defaultFont.getMetrics().lineHeight());

        this.renderText(text, model, color);
    }

    private void renderText(String text, Matrix4fc model, Vector4fc color) {
        this.textVertex.setModel(model);
        this.textVertex.setColor(color.x(), color.y(), color.z(), color.w());
        this.textVertex.setAtlasId(0);

        var it = new TextGlyphIterator(this.defaultFont, text);
        while (it.hasNext()) {
            var glyph = it.next();
            if (glyph == null)
                continue;

            this.renderGlyph(model, glyph);
        }
    }

    private void renderGlyph(Matrix4fc model, TextGlyphIterator.NextGlyph glyph) {
        assert this.renderedCharsCount < this.size : "exceeded batch size for text renderer";

        var positions = glyph.positions();
        var uvs = glyph.uvs();
        var offset = glyph.offset();
        for (int i = 0; i < 4; i++) {
            this.textVertex.setPosition(offset.x() + positions[i].x(), offset.y() + positions[i].y());
            this.textVertex.setUV(uvs[i].x(), uvs[i].y());
            this.textVertex.get(this.buffer);
        }

        this.renderedCharsCount += 1;
    }

    @Override
    public void endFrame(Matrix4fc projection, Matrix4fc view) {
        if (this.renderedCharsCount <= 0)
            return;

        var bufferSlice = this.buffer.slice(0, TextRenderer.VERTEX_SIZE * TextRenderer.VERTICES_PER_CHAR * this.renderedCharsCount);
        this.vbo.uploadData(bufferSlice);

        this.ibo.bind();
        this.vao.bind();
        this.defaultFont.getAtlasTexture().bindUnit(0);
        this.shader.bind();

        this.shader.setUniform("u_Projection", projection);
        this.shader.setUniform("u_View", view);
        this.shader.setUniform("u_UnitRange", this.defaultFont.getUnitRange());
        this.shader.setUniform("u_Atlas", 0);

        GL46.glDrawElements(GL46.GL_TRIANGLES, TextRenderer.INDICES_PER_CHAR * this.renderedCharsCount, GL46.GL_UNSIGNED_INT, 0);
    }

    @Override
    public void close() {
        this.vao.close();
        this.vbo.close();
        this.ibo.close();
    }
}
