package io.github.lionarius.client.component;

import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.scene.builtin.Transform;
import org.joml.Vector2f;
import org.joml.Vector3f;

public class CopyCameraSize extends Component {
    private Transform transform;

    @SerializeField
    private Camera camera;
    @SerializeField
    private boolean isWorldspace = true;
    @SerializeField
    private float sizePadding = 0;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onUpdate(double delta) {
        if (this.camera == null)
            return;

        if (this.isWorldspace) {
            var halfSize = this.camera.screenToWorldPosition(new Vector2f(-this.sizePadding, -this.sizePadding)).absolute();
            var size = halfSize.mul(2);
            this.transform.getSize().set(size);
        } else {
            this.transform.getSize().set(new Vector3f(this.camera.getFrameSize(), 0).sub(this.sizePadding, this.sizePadding, 0));
        }
    }
}
