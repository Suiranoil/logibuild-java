package io.github.lionarius.client.component;

import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.scene.Component;

@HideComponent
public class LifetimeComponent extends Component {
    private float lifetime;

    public LifetimeComponent(float lifetime) {
        this.lifetime = lifetime;
    }

    @Override
    public void onUpdate(double delta) {
        this.lifetime -= (float) delta;
        if (this.lifetime <= 0)
            this.getGameObject().getScene().removeGameObject(this.getGameObject());
    }
}
