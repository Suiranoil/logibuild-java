package io.github.lionarius.engine.resource;

import io.github.lionarius.engine.util.Closeable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

public interface Resource extends Closeable {
    String getResourceName();
    void setResourceName(String name);
}
