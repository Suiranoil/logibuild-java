package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.MinMax;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.util.ProjectionUtil;
import org.joml.*;

public class PerspectiveCamera extends Camera {
    @SerializeField @MinMax(min = 0.001f, max = 179.999f)
    private float fov = 90.0f;
    @SerializeField @Min(0.001f)
    private float far = 10000.0f;
    @SerializeField @Min(0.001f)
    private float near = 0.05f;

    @Override
    public void onUpdate(double delta) {
        this.getTransform().setSize(1, 1, 1);
    }

    @Override
    public Matrix4fc getProjection() {
        var aspect = (float) this.getFrameSize().x() / this.getFrameSize().y();
//        var aspect = 16.0f / 9.0f;
        return ProjectionUtil.getPerspectiveProjection(this.fov, aspect, this.far, this.near);
    }

    @Override
    public Matrix4fc getView() {
        var transformMatrix = this.getTransform().getTransformMatrix();
        var forward = new Vector4f(0, 0, 1, 1).mulProject(transformMatrix);
        var eye = new Vector4f().mulProject(transformMatrix);
        var top = new Vector4f(0, -1, 0, 1).mulProject(transformMatrix).sub(eye).normalize();

        return new Matrix4f().setLookAt(
                eye.x(), eye.y(), eye.z(),
                forward.x(), forward.y(), forward.z(),
                top.x(), top.y(), top.z()
        );
    }

    @Override
    public Vector3f screenToWorldPosition(Vector2fc screenPosition) {
        return new Vector3f(0);
    }
}
