package io.github.lionarius.engine.resource;

import java.io.IOException;

public interface ResourceLoader<T extends Resource> {
    T loadFromFile(String filepath, Object parameters) throws IOException;
}
