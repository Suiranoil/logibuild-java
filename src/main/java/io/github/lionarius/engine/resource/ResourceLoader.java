package io.github.lionarius.engine.resource;

import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;

import java.io.IOException;

public interface ResourceLoader<T extends Resource> {
    T loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException;
}
