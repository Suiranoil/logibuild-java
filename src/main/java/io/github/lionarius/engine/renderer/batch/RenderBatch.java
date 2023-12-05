package io.github.lionarius.engine.renderer.batch;

import io.github.lionarius.engine.util.Closeable;
import org.joml.Matrix4fc;

public interface RenderBatch extends Closeable {
    void init();

    void beginFrame();

    void endFrame(Matrix4fc projection, Matrix4fc view);
}
