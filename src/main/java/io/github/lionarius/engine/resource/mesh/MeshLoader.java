package io.github.lionarius.engine.resource.mesh;

import de.javagl.obj.Obj;
import de.javagl.obj.ObjReader;
import de.javagl.obj.ObjUtils;
import io.github.lionarius.engine.resource.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;

public class MeshLoader implements ResourceLoader<Mesh> {
    @Override
    public Mesh loadFromFile(String name, String filepath, Object parameters) throws IOException {
        Obj obj;

        try (var stream = new FileInputStream(filepath)) {
            var loaded = ObjReader.read(stream);
            obj = ObjUtils.convertToRenderable(loaded);
        }

        return null;
    }
}
