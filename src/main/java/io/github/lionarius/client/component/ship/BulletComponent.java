package io.github.lionarius.client.component.ship;

import io.github.lionarius.engine.editor.property.HideComponent;
import io.github.lionarius.engine.scene.Component;
import io.github.lionarius.engine.scene.builtin.collision.Collider;

@HideComponent
public class BulletComponent extends Component {

    @Override
    public void onCollide(Collider other) {
        this.getGameObject().getScene().removeGameObject(this.getGameObject());
    }
}
