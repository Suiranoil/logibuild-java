package io.github.lionarius.engine.scene.builtin;

import io.github.lionarius.engine.scene.Component;
import lombok.Getter;
import org.joml.Quaternionf;
import org.joml.Vector3f;

@Getter
public class Transform extends Component {
    private final Vector3f position = new Vector3f();
    private final Vector3f size = new Vector3f();
    private final Vector3f scale = new Vector3f(1);
    private final Quaternionf rotation = new Quaternionf();
}
