package io.github.lionarius.engine.scene;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
public abstract class Component {
    @Getter @Setter(AccessLevel.PROTECTED)
    private GameObject gameObject;

    public void onAwake() {
    }

    public void onStart() {
    }

    public void onUpdate(double delta) {
    }

    public void onRender(double delta) {
    }

    public void onDestroy() {
    }
}
