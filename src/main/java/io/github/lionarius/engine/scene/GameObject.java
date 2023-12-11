package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Transform;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.*;

public final class GameObject implements Updatable, Renderable {
    @Getter @Setter(AccessLevel.PROTECTED)
    private Scene scene = null;
    @Getter @Setter(AccessLevel.PRIVATE)
    private GameObject parent = null;
    private final Set<GameObject> children = new HashSet<>();
    @Getter
    private final Transform transform;
    private final List<Component> components = new ArrayList<>();

    private final Queue<Component> addedComponents = new ArrayDeque<>();
    private final Queue<Component> removedComponents = new ArrayDeque<>();

    public GameObject() {
        this.transform = new Transform();
        this.addComponent(this.transform);
    }

    public GameObject(@NonNull Transform transform) {
        this.transform = transform;
        this.addComponent(this.transform);
    }

    public GameObject(@NonNull Iterable<Component> components) {
        this();
        for (var component : components)
            this.addComponent(component);
    }

    public GameObject(@NonNull Transform transform, @NonNull Iterable<Component> components) {
        this(transform);
        for (var component : components)
            this.addComponent(component);
    }

    public Iterable<GameObject> getChildren() {
        return this.children;
    }

    public boolean isAncestor(GameObject gameObject) {
        if (this.parent == null)
            return false;

        if (this.parent == gameObject)
            return true;

        return this.parent.isAncestor(gameObject);
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

    public void addChild(GameObject gameObject) {
        if (this.scene != gameObject.getScene())
            return;

        if (this == gameObject)
            return;

        if (gameObject.getParent() != null)
            return;

        if (this.isAncestor(gameObject))
            return;

        this.children.add(gameObject);
        gameObject.setParent(this);
    }

    public void removeChild(GameObject gameObject) {
        if (!this.children.contains(gameObject))
            return;

        gameObject.setParent(null);
        this.children.remove(gameObject);
    }

    public void addComponent(@NonNull Component component) {
        if (this.scene == null || !this.scene.isEntered())
            this.processAddComponent(component);
        else
            this.addedComponents.add(component);
    }

    public void removeComponent(@NonNull Component component) {
        if (this.scene == null || !this.scene.isEntered())
            this.processRemoveComponent(component);
        else
            this.removedComponents.add(component);
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

    private void processAddComponent(Component component) {
        // Cannot add another transform component
        if (Transform.class.isAssignableFrom(component.getClass()) && !this.components.isEmpty())
            return;

        this.components.add(component);
        component.setGameObject(this);
    }

    private void processRemoveComponent(Component component) {
        // Cannot remove base transform component
        if (Transform.class.isAssignableFrom(component.getClass()))
            return;

        this.components.remove(component);
        component.setGameObject(null);
    }
}
