package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.util.ProjectionUtil;
import org.joml.Matrix4fc;
import org.joml.Vector2ic;

public class OrthoCamera extends Camera {
    public OrthoCamera(Vector2ic frameSize) {
        super(frameSize);
    }

    @Override
    public Matrix4fc getProjection() {
        return ProjectionUtil.getOrthoProjectionCentered(0, 0, this.getFrameSize().x(), this.getFrameSize().y());
    }

    @Override
    public Matrix4fc getView() {
        return this.getTransform().getTransformMatrix().invertAffine();
    }
}
