package io.github.lionarius.engine.renderer.quad;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.buffer.BufferUtil;
import io.github.lionarius.engine.util.buffer.GetToByteBuffer;
import lombok.RequiredArgsConstructor;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class QuadVertexCommon implements GetToByteBuffer {
    private static final VertexBufferLayout LAYOUT;

    static {
        LAYOUT = new VertexBufferLayout();
        LAYOUT.push(Float.class, 3); // vec3 position
        LAYOUT.push(Float.class, 2); // vec2 uv
    }

    private final Vector3f position;
    private final Vector2f uv;

    public static VertexBufferLayout getLayout() {
        return QuadVertexCommon.LAYOUT;
    }

    @Override
    public void get(ByteBuffer byteBuffer) {
        this.position.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 3 * Float.BYTES);

        this.uv.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 2 * Float.BYTES);
    }
}
