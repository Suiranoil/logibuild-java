package io.github.lionarius.engine.resource.impl.image;

import io.github.lionarius.engine.resource.Resource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.lwjgl.BufferUtils;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class Image implements Resource {
    @Getter @Setter
    private String resourceName;

    @Getter
    private final int width;
    @Getter
    private final int height;
    @Getter
    private final int channels;
    private ByteBuffer data;

    protected void init(ByteBuffer data) {
        this.data = BufferUtils.createByteBuffer(this.width * this.height * this.channels);
        this.data.put(data);
        this.data.rewind();
    }

    public ByteBuffer getData() {
        return this.data.asReadOnlyBuffer();
    }

    @Override
    public void close() {
    }
}
