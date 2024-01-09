package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.resource.impl.texture.Texture;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector4f;

public class Box2DRenderer extends Component {
    private final transient QuadRenderer renderer = Logibuild.getInstance().getEngineRenderer().getQuadRenderer();

    @NonNull @Getter @Setter
    @SerializeField
    private Vector4f color = new Vector4f(1);
    @Getter @Setter
    @SerializeField
    private Texture texture;

    @Override
    public void onRender(double delta) {
        var transform = this.getGameObject().getTransform();
        if (transform != null) {
            this.renderer.renderQuad(transform.getTransformMatrix(), transform.getSize(), this.color, this.texture);
        }
    }
}
