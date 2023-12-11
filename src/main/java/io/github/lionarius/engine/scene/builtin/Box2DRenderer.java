package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class Box2DRenderer extends Component {
    private final QuadRenderer renderer;
    private Transform transform;

    @NonNull @Getter @Setter
    private Vector4f color;

    public Box2DRenderer(QuadRenderer renderer) {
        this(renderer, new Vector4f(1));
    }

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onRender(double delta) {
        this.renderer.renderQuad(this.transform.getTransformMatrix(), this.transform.getSize(), this.color);
    }
}
