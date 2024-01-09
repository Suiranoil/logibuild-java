package io.github.lionarius.engine.resource.stream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class FilesystemStreamProvider extends ResourceStreamProvider {
    public FilesystemStreamProvider(String base) {
        super(base);
    }

    @Override
    public InputStream getStream(String path) throws IOException {
        return new FileInputStream(new File(this.getBase(), path));
    }
}
