package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.scene.Component;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

@HideComponent
public class EditorCameraControls extends Component {
    private final transient InputHandler inputHandler = Logibuild.getInstance().getInputHandler();
    private Transform transform;
    private OrthographicCamera camera;

    private float movementSpeed = 1.0f;
    private float rotationSpeed = 90.0f;
    private float sizeSpeed = 1.0f;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
        this.camera = this.getGameObject().getComponent(OrthographicCamera.class);
    }

    @Override
    public void onUpdate(double delta) {
        var movement = new Vector3f();
        var rotation = 0.0f;
        var size = 0.0f;

        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_W))
            movement.add(0, 1, 0);
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_A))
            movement.add(-1, 0, 0);
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_S))
            movement.add(0, -1, 0);
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_D))
            movement.add(1, 0, 0);

        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_Q))
            rotation -= this.rotationSpeed;
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_E))
            rotation += this.rotationSpeed;

        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT_SHIFT))
            size -= this.sizeSpeed;
        if (this.inputHandler.isKeyPressed(GLFW.GLFW_KEY_LEFT_CONTROL))
            size += this.sizeSpeed;

        if (movement.lengthSquared() > 0)
            movement.normalize();

        // adjust for inverted Y
        movement.mul(1, -1, 1);
        this.transform.getRotation().rotateZ((float) Math.toRadians(rotation * delta));
        movement.rotate(this.transform.getRotation());
        this.transform.getPosition().add(movement.mul((float) (this.movementSpeed * delta)));

        this.camera.setSize((float) (this.camera.getSize() + size * delta));
    }
}
