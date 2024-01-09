package io.github.lionarius.engine.resource.stream;

import java.io.IOException;
import java.io.InputStream;

public class ClasspathStreamProvider extends ResourceStreamProvider {
    public ClasspathStreamProvider(String base) {
        super(base);
    }

    @Override
    public InputStream getStream(String path) throws IOException {
        return ClassLoader.getSystemResource(this.getBase() + path).openStream();
    }
}
