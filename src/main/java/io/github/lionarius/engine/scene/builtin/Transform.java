package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
@NoArgsConstructor
public class Transform extends Component {
    private final Vector3f position = new Vector3f();
    private final Quaternionf rotation = new Quaternionf();
    private final Vector3f scale = new Vector3f(1);
    private final Vector3f size = new Vector3f();

    public void setPosition(float x, float y, float z) {
        this.position.set(x, y, z);
    }

    public void setScale(float x, float y, float z) {
        this.scale.set(x, y, z);
    }

    public void setSize(float x, float y, float z) {
        this.size.set(x, y, z);
    }

    public Matrix4f getTransformMatrix() {
        var matrix = new Matrix4f().translate(this.position).rotate(this.rotation).scale(this.scale);
        if (this.getGameObject().getParent() != null) {
            var parentTransform = this.getGameObject().getParent().getTransform();

            parentTransform.getTransformMatrix().mul(matrix, matrix);
        }
        return matrix;
    }
}
