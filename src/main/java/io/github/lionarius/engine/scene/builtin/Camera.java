package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.renderer.RenderCamera;
import io.github.lionarius.engine.scene.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector2fc;
import org.joml.Vector2i;
import org.joml.Vector3f;

public abstract class Camera extends Component implements RenderCamera {
    @NonNull @Getter @Setter @SerializeField
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
