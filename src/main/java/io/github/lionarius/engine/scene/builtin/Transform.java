package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class Transform extends Component {
    @SerializeField
    private final Vector3f position = new Vector3f();
    @SerializeField
    private final Quaternionf rotation = new Quaternionf();
    @SerializeField
    private final Vector3f scale = new Vector3f(1);
    @SerializeField
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
        return this.getParentTransformMatrix().mul(this.getLocalTransformMatrix());
    }

    public Matrix4f getLocalTransformMatrix() {
        return new Matrix4f().translate(this.position).rotate(this.rotation).scale(this.scale);
    }

    public Matrix4f getParentTransformMatrix() {
        if (this.getGameObject().getParent() != null) {
            var parentTransform = this.getGameObject().getParent().getTransform();

            return parentTransform.getTransformMatrix();
        }

        return new Matrix4f();
    }
}
