package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.util.ProjectionUtil;
import org.joml.Matrix4fc;

public class OrthoCamera extends Camera {
    public OrthoCamera(Window window) {
        super(window);
    }

    @Override
    public Matrix4fc getProjection() {
        return ProjectionUtil.getOrthoProjectionCentered(0, 0, this.getWindow().getWidth(), this.getWindow().getHeight());
    }

    @Override
    public Matrix4fc getView() {
        return this.getTransform().getTransformMatrix().invertAffine();
    }
}
