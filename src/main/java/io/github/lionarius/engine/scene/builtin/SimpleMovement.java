package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.editor.property.Min;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.scene.Component;
import org.joml.Vector3f;
import org.lwjgl.glfw.GLFW;

public class SimpleMovement extends Component {
    private transient final InputHandler inputHandler = Logibuild.getInstance().getInputHandler();
    @SerializeField @Min(0)
    private float movementSpeed = 800.0f;
    @SerializeField @Min(0)
    private float rotationSpeed = 90.0f;
    @SerializeField
    private Transform transform;

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

        if (movement.lengthSquared() > 0)
            movement.normalize();

        // adjust for inverted Y
        movement.mul(1, -1, 1);
        this.transform.getRotation().rotateZ((float) Math.toRadians(rotation * delta));
        movement.rotate(this.transform.getRotation());
        this.transform.getPosition().add(movement.mul((float) (this.movementSpeed * delta)));
    }
}
