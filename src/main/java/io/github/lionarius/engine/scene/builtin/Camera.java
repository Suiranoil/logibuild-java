package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.scene.Component;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4fc;

@RequiredArgsConstructor
public abstract class Camera extends Component {
    @Getter(AccessLevel.PROTECTED)
    private final Window window;
    @Getter(AccessLevel.PROTECTED)
    private Transform transform;

    @Override
    public final void onAwake() {
        this.transform = this.getGameObject().getComponent(Transform.class);

        assert this.transform != null;
        this.transform.getSize().set(1);
    }

    public abstract Matrix4fc getProjection();

    public abstract Matrix4fc getView();
}
