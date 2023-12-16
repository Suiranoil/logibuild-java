package io.github.lionarius.engine.scene;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@NoArgsConstructor
public abstract class Component {
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter @Setter(AccessLevel.PROTECTED)
    private transient GameObject gameObject;

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

    @Override
    public final int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public final boolean equals(Object obj) {
        return obj instanceof Component c && this.uuid.equals(c.uuid);
    }
}
