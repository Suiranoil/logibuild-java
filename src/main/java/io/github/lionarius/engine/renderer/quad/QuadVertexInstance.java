package io.github.lionarius.engine.renderer.quad;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.buffer.BufferUtil;
import io.github.lionarius.engine.util.buffer.GetToByteBuffer;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class QuadVertexInstance implements GetToByteBuffer {
    private static final VertexBufferLayout LAYOUT;

    static {
        LAYOUT = new VertexBufferLayout();
        LAYOUT.push(Float.class, 4); // vec4 color
        LAYOUT.push(Float.class, 1); // float textureId
        LAYOUT.push(Matrix4f.class, 1); // mat 4 model
    }

    public static VertexBufferLayout getLayout() {
        return QuadVertexInstance.LAYOUT;
    }


    private final Vector4f color = new Vector4f();
    @Setter
    private float textureId = -1.0f;
    private final Matrix4f model = new Matrix4f();

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    public void setModel(Matrix4fc model) {
        this.model.set(model);
    }

    @Override
    public void get(ByteBuffer byteBuffer) {
        this.color.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 4 * Float.BYTES);

        byteBuffer.asFloatBuffer().put(this.textureId);
        BufferUtil.movePosition(byteBuffer, Float.BYTES);

        this.model.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 4 * 4 * Float.BYTES);
    }
}
