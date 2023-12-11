package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.scene.Component;
import lombok.*;
import org.joml.Matrix4fc;
import org.joml.Vector2ic;

@RequiredArgsConstructor
public abstract class Camera extends Component {
    @NonNull @Getter @Setter
    private Vector2ic frameSize;
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

    public abstract Matrix4fc getProjection();

    public abstract Matrix4fc getView();
}
