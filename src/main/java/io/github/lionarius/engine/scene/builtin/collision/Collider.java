package io.github.lionarius.engine.scene.builtin.collision;

import io.github.lionarius.engine.editor.property.SerializeField;
import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import lombok.Setter;
import org.joml.Vector4f;

public abstract class Collider extends Component {
    @SerializeField @Getter @Setter
    private String collisionGroup = "Default";
    @SerializeField @Getter @Setter
    private String ignoreGroup = "_";

    protected final transient Vector4f debugColor = new Vector4f(0, 1, 0, 1);
}
