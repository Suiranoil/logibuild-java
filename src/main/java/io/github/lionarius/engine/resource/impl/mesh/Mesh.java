package io.github.lionarius.engine.resource.impl.mesh;

import io.github.lionarius.engine.resource.Resource;
import lombok.Getter;
import lombok.Setter;

public class Mesh implements Resource {
    @Getter @Setter
    private String resourceName;

    @Override
    public void close() {

    }
}
