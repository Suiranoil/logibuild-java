package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.MinMax;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.util.ProjectionUtil;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Vector2fc;
import org.joml.Vector3f;

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
        var transform = this.getTransform();
        var forward = new Vector3f(0, 0, 1);
        var top = new Vector3f(0, -1, 0);
        forward.rotate(transform.getRotation()).add(transform.getPosition());
        top.rotate(transform.getRotation());
        return new Matrix4f().setLookAt(
                transform.getPosition(),
                forward,
                top
        );
    }

    @Override
    public Vector3f screenToWorldPosition(Vector2fc screenPosition) {
        return new Vector3f(0);
    }
}
