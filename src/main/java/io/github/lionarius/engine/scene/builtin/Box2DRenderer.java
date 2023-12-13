package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.resource.texture.Texture;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class Box2DRenderer extends Component {
    private final QuadRenderer renderer = Logibuild.getInstance().getEngineRenderer().getQuadRenderer();
    private Transform transform;

    @NonNull @Getter @Setter
    private Vector4f color;
    @Getter @Setter
    private Texture texture;

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onRender(double delta) {
        this.renderer.renderQuad(this.transform.getTransformMatrix(), this.transform.getSize(), this.color, this.texture);
    }
}
