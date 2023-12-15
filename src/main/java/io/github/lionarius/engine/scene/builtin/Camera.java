package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.scene.CameraObject;
import io.github.lionarius.engine.scene.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector3f;

public abstract class Camera extends Component implements CameraObject {
    @NonNull @Getter @Setter
    private Vector2i frameSize = new Vector2i(0);
    @Getter(AccessLevel.PROTECTED)
    private Transform transform;

    @Override
    public final void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        this.transform.getSize().set(this.frameSize, 0);
    }

    public abstract Vector3f screenToWorldPosition(Vector2fc screenPosition);
}
