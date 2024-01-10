package io.github.lionarius.engine.resource.stream;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceStreamProvider {
    @Getter
    private final String base;

    protected ResourceStreamProvider(String base) {
        this.base = base;
    }

    // CLOSE IT DAMMIT
    public abstract InputStream getStream(String path) throws IOException;
}
