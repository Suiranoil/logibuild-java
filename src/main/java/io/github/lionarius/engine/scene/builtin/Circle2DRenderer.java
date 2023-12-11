package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.renderer.circle.CircleRenderer;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class Circle2DRenderer extends Component {
    private final CircleRenderer renderer = Logibuild.getInstance().getEngineRenderer().getCircleRenderer();
    private Transform transform;

    @NonNull @Getter @Setter
    private Vector4f color;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onRender(double delta) {
        this.renderer.renderCircle(this.transform.getTransformMatrix(), this.transform.getSize(), this.color);
    }
}
