package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.util.Closeable;
import org.joml.Matrix4fc;

public interface Renderer extends Closeable {
    void init();

    void beginFrame();

    void endFrame(Matrix4fc projection, Matrix4fc view);
}
