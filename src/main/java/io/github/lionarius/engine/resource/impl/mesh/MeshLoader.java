package io.github.lionarius.engine.resource.impl.mesh;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import io.github.lionarius.engine.resource.ResourceLoader;
import io.github.lionarius.engine.resource.stream.ResourceStreamProvider;

import java.io.IOException;

public class MeshLoader implements ResourceLoader<Mesh> {
    @Override
    public Mesh loadFromFile(String name, ResourceStreamProvider streamProvider, Object parameters) throws IOException {
        Obj obj;

        try (var stream = streamProvider.getStream(name)) {
            var loaded = ObjReader.read(stream);
            obj = ObjUtils.convertToRenderable(loaded);
        }

        return null;
    }
}
