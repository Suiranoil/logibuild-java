package io.github.lionarius.engine.resource;

import java.io.IOException;

public interface ResourceLoader<T extends Resource> {
    T loadFromFile(String name, String filepath, Object parameters) throws IOException;
}
