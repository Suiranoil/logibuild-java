package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.renderer.quad.QuadRenderer;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class Box2DRenderer extends Component {
    private final QuadRenderer renderer;
    private Transform transform;

    @Getter
    private final Vector4f color;

    public Box2DRenderer(QuadRenderer renderer) {
        this(renderer, new Vector4f());
    }

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getComponent(Transform.class);
    }

    @Override
    public void onRender(double delta) {
        this.renderer.renderQuad(this.transform.getTransformMatrix(), this.transform.getSize(), this.color);
    }
}
