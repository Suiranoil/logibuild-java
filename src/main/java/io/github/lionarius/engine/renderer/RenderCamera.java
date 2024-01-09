package io.github.lionarius.engine.renderer;

import org.joml.Matrix4fc;
import org.joml.Vector2i;

public interface RenderCamera {
    Matrix4fc getProjection();

    Matrix4fc getView();

    void setFrameSize(Vector2i frameSize);
}
