package io.github.lionarius.engine.resource.impl.raw;

import io.github.lionarius.engine.resource.Resource;
import lombok.Getter;
import lombok.Setter;

import java.nio.ByteBuffer;

public class RawData implements Resource {
    @Getter @Setter
    private String resourceName;
    private final ByteBuffer buffer;

    protected RawData(ByteBuffer buffer) {
        this.buffer = buffer;
    }

    public ByteBuffer getData() {
        return this.buffer.asReadOnlyBuffer();
    }

    @Override
    public void close() {
    }
}
