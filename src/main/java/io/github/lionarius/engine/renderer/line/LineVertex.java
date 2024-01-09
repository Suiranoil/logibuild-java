package io.github.lionarius.engine.renderer.line;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.GetToByteBuffer;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

public class LineVertex implements GetToByteBuffer {
    private static final VertexBufferLayout LAYOUT;

    static {
        LAYOUT = new VertexBufferLayout();
        LAYOUT.push(Float.class, 3); // vec3 position
        LAYOUT.push(Float.class, 4); // vec4 color
    }

    public static VertexBufferLayout getLayout() {
        return LineVertex.LAYOUT;
    }


    private final Vector3f position = new Vector3f();
    private final Vector4f color = new Vector4f();

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void setColor(float r, float g, float b, float a) {
        this.color.set(r, g, b, a);
    }

    @Override
    public void get(ByteBuffer byteBuffer) {
        this.position.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 3 * Float.BYTES);

        this.color.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 4 * Float.BYTES);
    }
}
