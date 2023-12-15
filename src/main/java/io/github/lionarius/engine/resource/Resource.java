package io.github.lionarius.engine.resource;

import io.github.lionarius.engine.util.Closeable;

public interface Resource extends Closeable {
    String getResourceName();

    void setResourceName(String name);
}
