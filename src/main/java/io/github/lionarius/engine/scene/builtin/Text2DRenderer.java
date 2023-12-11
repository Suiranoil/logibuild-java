package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.renderer.text.TextRenderer;
import io.github.lionarius.engine.resource.font.Font;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.joml.Vector4f;

@RequiredArgsConstructor
public class Text2DRenderer extends Component {
    private final TextRenderer renderer;
    private Transform transform;

    @NonNull @Getter @Setter
    private String text;
    @NonNull @Getter @Setter
    private Vector4f color;
    @Getter @Setter
    private Font font = null;

    public Text2DRenderer(TextRenderer renderer) {
        this(renderer, "", new Vector4f(1));
    }

    @Override
    public void onAwake() {
        this.transform = this.getGameObject().getTransform();
    }

    @Override
    public void onRender(double delta) {
        this.renderer.renderText(this.text, this.font, this.transform.getTransformMatrix(), this.transform.getSize().y(), this.color);
    }
}
