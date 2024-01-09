package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.util.ProjectionUtil;
import lombok.Getter;
import lombok.Setter;
import org.joml.Matrix4fc;
import org.joml.Vector2f;
import org.joml.Vector2fc;
import org.joml.Vector3f;

public class OrthographicCamera extends Camera {
    public enum Axis {
        X,
        Y
    }

    @SerializeField @Min(0.001f) @Getter @Setter
    private float size = 1.0f;
    private float aspect = 1.0f;
    @SerializeField
    private Axis mainAxis = Axis.Y;

    @Override
    public void onUpdate(double delta) {
        this.aspect = (float) this.getFrameSize().x() / this.getFrameSize().y();
    }

    @Override
    public Matrix4fc getProjection() {
        if (this.mainAxis == Axis.X)
            return ProjectionUtil.getOrthoProjectionCentered(0, 0, this.size, this.size / this.aspect);
        else
            return ProjectionUtil.getOrthoProjectionCentered(0, 0, this.size * this.aspect, this.size);
    }

    @Override
    public Matrix4fc getView() {
        return this.getTransform().getTransformMatrix().invertAffine();
    }

    @Override
    public Vector3f screenToWorldPosition(Vector2fc screenPosition) {
        var offsetPosition = this.mainAxis == Axis.X ? new Vector2f(this.size, this.size / this.aspect) : new Vector2f(this.size * this.aspect, this.size);
        offsetPosition.div(2);

        return new Vector3f(screenPosition.sub(offsetPosition, offsetPosition), 0).mulProject(this.getTransform().getTransformMatrix());
    }
}
