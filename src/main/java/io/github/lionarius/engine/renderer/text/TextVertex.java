package io.github.lionarius.engine.renderer.text;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.GetToByteBuffer;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class TextVertex implements GetToByteBuffer {
    private static final VertexBufferLayout LAYOUT;

    static {
        LAYOUT = new VertexBufferLayout();
        LAYOUT.push(Float.class, 2); // vec2 position
        LAYOUT.push(Float.class, 2); // vec2 uv
        LAYOUT.push(Float.class, 4); // vec4 color
        LAYOUT.push(Matrix4f.class, 1); // mat4 model
    }

    public static VertexBufferLayout getLayout() {
        return TextVertex.LAYOUT;
    }


    private final Vector2f position = new Vector2f();
    private final Vector2f uv = new Vector2f();
    private final Vector4f color = new Vector4f();
    private final Matrix4f model = new Matrix4f();

    public void setPosition(float x, float y) {
        this.position.set(x, y);
    }

    public void setUV(float x, float y) {
        this.uv.set(x, y);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void setModel(Matrix4fc model) {
        this.model.set(model);
    }

    @Override
    public void get(ByteBuffer byteBuffer) {
        this.position.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 2 * Float.BYTES);

        this.uv.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 2 * Float.BYTES);

        this.color.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 4 * Float.BYTES);

        this.model.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 4 * 4 * Float.BYTES);
    }
}
