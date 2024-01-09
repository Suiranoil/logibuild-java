package io.github.lionarius.client.component.asteroid;

import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.scene.builtin.collision.Collider;
import lombok.Getter;

@HideComponent
public class AsteroidComponent extends Component {
    private Transform transform;
    private final AsteroidSpawner asteroidSpawner;
    @Getter
    private final float rotationSpeed;

    public AsteroidComponent(AsteroidSpawner asteroidSpawner, float rotationSpeed) {
        this.asteroidSpawner = asteroidSpawner;
        this.rotationSpeed = rotationSpeed;
    }

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        this.transform.getRotation().rotateZ((float) Math.toRadians(this.rotationSpeed * delta));
    }

    @Override
    public void onCollide(Collider other) {
        if (other.getCollisionGroup().equals("bullet"))
            this.asteroidSpawner.processDestroyedAsteroid(this);

        this.getGameObject().getScene().removeGameObject(this.getGameObject());
    }
}
