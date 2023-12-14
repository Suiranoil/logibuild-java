package io.github.lionarius.engine.scene;

import org.joml.Matrix4fc;
import org.joml.Vector2i;

public interface CameraObject {
    Matrix4fc getProjection();

    Matrix4fc getView();

    void setFrameSize(Vector2i frameSize);
}
