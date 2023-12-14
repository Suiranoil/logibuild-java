package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.util.ProjectionUtil;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    @Override
    public Matrix4fc getProjection() {
        return ProjectionUtil.getOrthoProjectionCentered(0, 0, this.getFrameSize().x(), this.getFrameSize().y());
    }

    @Override
    public Matrix4fc getView() {
        return this.getTransform().getTransformMatrix().invertAffine();
    }

    @Override
    public Vector3f screenToWorldPosition(Vector2fc screenPosition) {
        var offsetPosition = new Vector2f(this.getFrameSize()).div(2);

        return new Vector3f(screenPosition.sub(offsetPosition, offsetPosition), 0).mulProject(this.getTransform().getTransformMatrix());
    }
}
