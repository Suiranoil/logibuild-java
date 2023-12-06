package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Transform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public final class GameObject implements Updatable, Renderable {
    @Getter @Setter(AccessLevel.PROTECTED)
    private Scene scene = null;
    private final List<Component> components = new ArrayList<>();
    private final Queue<Component> addedComponents = new ArrayDeque<>();
    private final Queue<Component> removedComponents = new ArrayDeque<>();

    public GameObject() {
        this.addComponent(new Transform());
    }

    public void addComponent(Component component) {
        if (this.scene == null || !this.scene.isEntered())
            this.processAddComponent(component);
        else
            this.addedComponents.add(component);
    }

    public void removeComponent(Component component) {
        if (this.scene == null || !this.scene.isEntered())
            this.processRemoveComponent(component);
        else
            this.removedComponents.add(component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> clazz) {
        for (var component : this.components) {
            if (clazz.isAssignableFrom(component.getClass()))
                return (T) component;
        }

        return null;
    }

    public void awake() {
        for (var component : this.components)
            component.onAwake();
    }

    public void start() {
        for (var component : this.components)
            component.onStart();
    }

    @Override
    public void update(double delta) {
        this.removeQueuedComponents();
        this.addQueuedComponents();

        for (var component : this.components)
            component.onUpdate(delta);
    }

    @Override
    public void render(double delta) {
        for (var component : this.components)
            component.onRender(delta);
    }

    public void destroy() {
        for (var component : this.components)
            component.onDestroy();
    }

    private void processAddComponent(Component component) {
        this.components.add(component);
        component.setGameObject(this);
    }

    private void processRemoveComponent(Component component) {
        this.components.remove(component);
        component.setGameObject(null);
    }

    private void addQueuedComponents() {
        if (!this.addedComponents.isEmpty()) {
            Component component;
            while ((component = this.addedComponents.poll()) != null) {
                this.processAddComponent(component);

                component.onAwake();
                component.onStart();
            }
        }
    }

    private void removeQueuedComponents() {
        if (!this.removedComponents.isEmpty()) {
            Component component;
            while ((component = this.removedComponents.poll()) != null) {
                component.onDestroy();

                this.processRemoveComponent(component);
            }
        }
    }
}
