package io.github.lionarius.engine.renderer.circle;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.GetToByteBuffer;
import lombok.RequiredArgsConstructor;
import org.joml.Vector3f;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class CircleVertexCommon implements GetToByteBuffer {
    private static final VertexBufferLayout LAYOUT;

    static {
        LAYOUT = new VertexBufferLayout();
        LAYOUT.push(Float.class, 3); // vec3 position
    }

    private final Vector3f position;

    public static VertexBufferLayout getLayout() {
        return CircleVertexCommon.LAYOUT;
    }

    @Override
    public void get(ByteBuffer byteBuffer) {
        this.position.get(byteBuffer);
        BufferUtil.movePosition(byteBuffer, 3 * Float.BYTES);
    }
}
