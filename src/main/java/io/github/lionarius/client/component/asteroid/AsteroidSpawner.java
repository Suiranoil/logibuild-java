package io.github.lionarius.client.component.asteroid;

import io.github.lionarius.client.component.LifetimeComponent;
import io.github.lionarius.client.component.MoveInDirection;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.Separator;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.builtin.Box2DRenderer;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.scene.builtin.collision.Circle2DCollider;
import org.joml.Vector3f;

import java.util.List;
import java.util.Random;

public class AsteroidSpawner extends Component {
    private Transform transform;
    private transient double time = 0.0;
    private transient final Random random = new Random();

    @SerializeField
    private Texture asteroidTexture;
    @SerializeField
    private float asteroidLifetime = 10;
    @SerializeField
    private double spawnRate = 0.5;
    @SerializeField @Separator
    private float directionVariance = 0.5f;
    @SerializeField @Min(0)
    private float minSpeed = 0.1f;
    @SerializeField @Min(0) @Separator
    private float maxSpeed = 0.5f;
    @SerializeField @Min(0)
    private float minSize = 0.05f;
    @SerializeField @Min(0) @Separator
    private float maxSize = 0.15f;
    @SerializeField
    private float minRotationSpeed = -360f;
    @SerializeField @Separator
    private float maxRotationSpeed = 360f;
    @SerializeField @Min(0)
    private int minDestroyAsteroidSpawn = 2;
    @SerializeField @Min(0)
    private int maxDestroyAsteroidSpawn = 4;
    @SerializeField @Min(0)
    private float minDestroySize = 0.1f;
    @SerializeField @Min(0f)
    private float minDestroyKeepSpeed = 0.6f;
    @SerializeField @Min(0f)
    private float maxDestroyKeepSpeed = 0.8f;
    @SerializeField @Min(0f)
    private float minDestroyKeepSize = 0.6f;
    @SerializeField @Min(0f)
    private float maxDestroyKeepSize = 0.8f;
    @SerializeField @Min(0f)
    private float minDestroyDirectionVariance = 0.3f;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        this.time += delta;
        if (this.time >= this.spawnRate) {
            this.time -= this.spawnRate;

            var position = this.getSpawnPosition();
            var direction = this.getAsteroidDirection(position);
            var speed = this.random.nextFloat(this.minSpeed, this.maxSpeed);
            var size = this.random.nextFloat(this.minSize, this.maxSize);
            var rotationSpeed = this.random.nextFloat(this.minRotationSpeed, this.maxRotationSpeed);
            this.spawnAsteroid(position, direction, speed / (size * 8), size, rotationSpeed / (size * 10));
        }
    }

    private Vector3f getSpawnPosition() {
        var size = this.transform.getSize();
        var lt = new Vector3f(-size.x / 2, -size.y / 2, 0);
        var rt = new Vector3f(size.x / 2, -size.y / 2, 0);
        var rb = new Vector3f(size.x / 2, size.y / 2, 0);
        var lb = new Vector3f(-size.x / 2, size.y / 2, 0);

        var t = this.random.nextFloat();
        var side = this.random.nextInt(4);

        return switch (side) {
            case 0 -> lt.lerp(rt, t, new Vector3f());
            case 1 -> rt.lerp(rb, t, new Vector3f());
            case 2 -> lb.lerp(rb, t, new Vector3f());
            case 3 -> lt.lerp(lb, t, new Vector3f());
            default -> new Vector3f();
        };
    }

    private Vector3f getAsteroidDirection(Vector3f position) {
        var t = this.random.nextFloat(-this.directionVariance, this.directionVariance);
        var varianceVector = position.cross(new Vector3f(0, 0, 1), new Vector3f()).normalize().mul(t);
        return position.mul(-1, new Vector3f()).add(varianceVector).normalize();
    }

    private void spawnAsteroid(Vector3f position, Vector3f direction, float speed, float size, float rotationSpeed) {
        var asteroidComponent = new AsteroidComponent(this, rotationSpeed);
        var lifetimeComponent = new LifetimeComponent(this.asteroidLifetime);
        var collider = new Circle2DCollider();
        collider.setPrecision(10);
        collider.setWidth(size);
        collider.setHeight(size);
        collider.setCollisionGroup("asteroid");
        collider.setIgnoreGroup("asteroid");

        var box2DRenderer = new Box2DRenderer();
        box2DRenderer.setTexture(this.asteroidTexture);

        var moveDirectionComponent = new MoveInDirection(direction, speed);

        var asteroid = new GameObject(List.of(asteroidComponent, collider, box2DRenderer, moveDirectionComponent, lifetimeComponent));
        asteroid.setName("Asteroid");
        asteroid.getTransform().getPosition().set(position);
        asteroid.getTransform().getSize().set(size, size, 0);
        var scaleX = this.random.nextInt(-1, 2);
        if (scaleX == 0)
            scaleX = 1;
        asteroid.getTransform().setScale(scaleX, 1, 1);

        this.getGameObject().getScene().addGameObject(asteroid);
        this.getGameObject().addChild(asteroid);
    }

    protected void processDestroyedAsteroid(AsteroidComponent asteroid) {
        if (this.minDestroyAsteroidSpawn <= 0)
            return;

        if (asteroid.getGameObject().getTransform().getSize().x() < this.minDestroySize)
            return;

        var moveComponent = asteroid.getGameObject().getComponent(MoveInDirection.class);
        assert moveComponent != null;

        var newAsteroidCount = this.random.nextInt(this.minDestroyAsteroidSpawn, this.maxDestroyAsteroidSpawn + 1);

        for (int i = 0; i < newAsteroidCount; i++) {
            var t = this.random.nextFloat(this.minDestroyDirectionVariance, this.minDestroyDirectionVariance + this.directionVariance);
            t = Math.copySign(t, this.random.nextFloat(-1, 1));

            var varianceVector = moveComponent.getDirection().cross(new Vector3f(0, 0, 1), new Vector3f()).normalize().mul(t);
            varianceVector.add(moveComponent.getDirection()).normalize();

            var size = asteroid.getGameObject().getTransform().getSize().x() / newAsteroidCount;
            size *= this.random.nextFloat(this.minDestroyKeepSize, this.maxDestroyKeepSize);

            var speed = moveComponent.getSpeed() / newAsteroidCount;
            speed *= this.random.nextFloat(this.minDestroyKeepSpeed, this.maxDestroyKeepSpeed);

            var rotationSpeed = this.random.nextFloat(this.minRotationSpeed, this.maxRotationSpeed);

            this.spawnAsteroid(asteroid.getGameObject().getTransform().getPosition(), varianceVector, speed, size, rotationSpeed);
        }
    }
}
