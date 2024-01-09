package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.Logibuild;
import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.renderer.text.TextRenderer;
import io.github.lionarius.engine.resource.impl.font.Font;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.joml.Vector4f;

public class Text2DRenderer extends Component {
    private final transient TextRenderer renderer = Logibuild.getInstance().getEngineRenderer().getTextRenderer();

    @NonNull @Getter @Setter
    @SerializeField
    private String text = "";
    @NonNull @Getter @Setter
    @SerializeField
    private Vector4f color = new Vector4f(1);
    @Getter @Setter
    @SerializeField
    private Font font = null;


    @Override
    public void onRender(double delta) {
        var transform = this.getGameObject().getTransform();
        if (transform != null)
            this.renderer.renderText(this.text, this.font, transform.getTransformMatrix(), transform.getSize().y(), this.color);
    }
}
