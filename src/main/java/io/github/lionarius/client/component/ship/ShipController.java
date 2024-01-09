package io.github.lionarius.client.component.ship;

import io.github.lionarius.Logibuild;
import io.github.lionarius.client.component.LifetimeComponent;
import io.github.lionarius.client.component.MoveInDirection;
import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.Separator;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.GameObject;
import io.github.lionarius.engine.scene.builtin.Box2DRenderer;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.scene.builtin.collision.Box2DCollider;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class ShipController extends Component {
    private final transient InputHandler inputHandler = Logibuild.getInstance().getInputHandler();
    private Transform transform;
    @SerializeField
    private final Vector3f velocity = new Vector3f(0);
    @SerializeField @Separator
    private float rotation = 0.0f;

    @SerializeField
    private Camera visibilityCamera;
    @SerializeField @Min(1)
    private float backwardMovementPenalty = 2f;
    @SerializeField @Min(0)
    private float movementAcceleration = 0.3f;
    @SerializeField @Min(0)
    private float maxMovementSpeed = 1.0f;
    @SerializeField @Min(0) @Separator
    private float velocityDrag = 0.05f;
    @SerializeField @Min(0)
    private float rotationAcceleration = 90f;
    @SerializeField @Min(0)
    private float maxRotationSpeed = 140.0f;
    @SerializeField @Min(0) @Separator
    private float rotationDrag = 0.05f;
    @SerializeField
    private Texture bulletTexture;
    @SerializeField
    private final Vector3f bulletSpawnOffset = new Vector3f();
    @SerializeField
    private final Vector3f bulletSize = new Vector3f();
    @SerializeField @Min(0)
    private float bulletSpeed = 0.5f;
    @SerializeField @Min(0)
    private float bulletLifetime = 2f;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        var movement = new Vector3f();
        var rotation = 0.0f;

        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_W))
            movement.add(0, 1, 0);
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_S))
            movement.add(0, -1, 0);

        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_A))
            rotation -= this.rotationAcceleration;
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_D))
            rotation += this.rotationAcceleration;

        this.rotation += (float) (rotation * delta);
        this.rotation = (float) (this.rotation - Math.signum(this.rotation) * this.rotationDrag * delta);
        if (Math.abs(this.rotation) > this.maxRotationSpeed)
            this.rotation = Math.copySign(this.maxRotationSpeed, this.rotation);
        this.transform.getRotation().rotateZ((float) Math.toRadians(this.rotation * delta));

        movement.mul(1, -1, 1);
        if (movement.y() > 0)
            movement.div(1, this.backwardMovementPenalty, 1);
        movement.rotate(this.transform.getRotation());
        this.velocity.add(movement.mul((float) delta * this.movementAcceleration));

        var cameraBounds = this.visibilityCamera.getGameObject().getTransform().getSize();
        if (cameraBounds.lengthSquared() > 0 && (Math.abs(this.transform.getPosition().x()) >= cameraBounds.x() / 2 || Math.abs(this.transform.getPosition().y()) >= cameraBounds.y() / 2))
            this.velocity.add(this.transform.getPosition().mul(-1, -1, 0, new Vector3f()).normalize().mul((float) (delta * this.movementAcceleration * 1.4)));

        var velocity = this.velocity.length();
        velocity = (float) Math.min(Math.max(0, velocity - this.velocityDrag * delta), this.maxMovementSpeed);
        if (velocity == 0.0f)
            this.velocity.mul(0);
        else
            this.velocity.normalize(velocity);

        var deltaPosition = this.velocity.mul((float) delta, new Vector3f());
        this.transform.getPosition().add(deltaPosition);

        if (this.inputHandler.isKeyJustPressed(GLFW.GLFW_KEY_SPACE))
            this.shootBullet();
    }

    private void shootBullet() {
        var bulletComponent = new BulletComponent();
        var lifetimeComponent = new LifetimeComponent(this.bulletLifetime);
        var collider = new Box2DCollider();
        collider.setWidth(this.bulletSize.x());
        collider.setHeight(this.bulletSize.y());
        collider.setCollisionGroup("bullet");
        collider.setIgnoreGroup("ship");

        var box2DRenderer = new Box2DRenderer();
        box2DRenderer.setTexture(this.bulletTexture);

        var matrix = this.transform.getTransformMatrix();
        var moveDir = new Vector4f(0, -1, 0, 1).mulProject(matrix, new Vector3f()).sub(this.transform.getPosition()).mul(1, 1, 0).normalize();
        var moveDirectionComponent = new MoveInDirection(moveDir, this.bulletSpeed);

        var bullet = new GameObject(List.of(bulletComponent, collider, box2DRenderer, moveDirectionComponent, lifetimeComponent));
        bullet.setName("Bullet");
        bullet.getTransform().getRotation().set(this.transform.getRotation());
        var spawnPos = new Vector4f(this.bulletSpawnOffset, 1).mulProject(matrix, new Vector3f());
        bullet.getTransform().getPosition().set(spawnPos);
        bullet.getTransform().getScale().set(this.transform.getScale());
        bullet.getTransform().getSize().set(this.bulletSize);

        this.getGameObject().getScene().addGameObject(bullet);
    }
}
