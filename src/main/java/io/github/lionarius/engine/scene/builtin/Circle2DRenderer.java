package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.renderer.circle.CircleRenderer;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector4f;

public class Circle2DRenderer extends Component {
    private transient final CircleRenderer renderer = Logibuild.getInstance().getEngineRenderer().getCircleRenderer();

    @NonNull @Getter @Setter
    @SerializeField
    private Vector4f color = new Vector4f(1);

    @Override
    public void onRender(double delta) {
        var transform = this.getGameObject().getTransform();
        if (transform != null)
            this.renderer.renderCircle(transform.getTransformMatrix(), transform.getSize(), this.color);
    }
}
