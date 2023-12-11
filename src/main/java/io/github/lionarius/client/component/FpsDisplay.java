package io.github.lionarius.client.component;

import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.builtin.Text2DRenderer;

public class FpsDisplay extends Component {
    private Text2DRenderer text2DRenderer;

    @Override
    public void onAwake() {
        this.text2DRenderer = this.getGameObject().getComponent(Text2DRenderer.class);
    }

    @Override
    public void onUpdate(double delta) {
        if (this.getGameObject().getParent() != null) {
            var parentTransform = this.getGameObject().getParent().getTransform();

            parentTransform.getSize().div(-2, this.getGameObject().getTransform().getPosition());
        }
        this.text2DRenderer.setText(String.format("%.3f FPS", 1.0 / delta));
    }
}
