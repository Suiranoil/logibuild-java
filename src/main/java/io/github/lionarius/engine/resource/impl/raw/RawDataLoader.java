package io.github.lionarius.engine.resource.impl.raw;

import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;
import io.github.lionarius.engine.util.io.StreamUtil;

import java.io.IOException;
import java.nio.ByteBuffer;

public class RawDataLoader implements ResourceLoader<RawData> {
    @Override
    public RawData loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        ByteBuffer data;

        try (var stream = streamProvider.getStream(name)) {
            data = StreamUtil.readStreamToBuffer(stream);
        }
        data.rewind();
        return new RawData(data);
    }
}
