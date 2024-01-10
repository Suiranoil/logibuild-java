package io.github.lionarius.engine.renderer;

import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2i;

public class ScreenspaceCamera implements RenderCamera {
    private static final Matrix4f IDENTITY = new Matrix4f();

    @Override
    public Matrix4fc getProjection() {
        return ScreenspaceCamera.IDENTITY;
    }

    @Override
    public Matrix4fc getView() {
        return ScreenspaceCamera.IDENTITY;
    }

    @Override
    public void setFrameSize(Vector2i frameSize) {
    }
}
