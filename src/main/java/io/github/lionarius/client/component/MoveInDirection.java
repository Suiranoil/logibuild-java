package io.github.lionarius.client.component;

import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.builtin.Transform;
import lombok.Getter;
import org.joml.Vector3f;

@HideComponent
public class MoveInDirection extends Component {
    private Transform transform;

    @Getter
    private final Vector3f direction;
    @Getter
    private final float speed;

    public MoveInDirection(Vector3f direction, float speed) {
        this.direction = direction;
        this.speed = speed;
    }

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        this.transform.getPosition().add(this.direction.mul((float) delta * this.speed, new Vector3f()));
    }
}
