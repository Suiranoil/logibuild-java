package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.util.AddRemoveQueue;
import io.github.lionarius.engine.util.ReflectionUtil;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class GameObject implements Updatable, Renderable {
    @Getter
    private final UUID uuid = UUID.randomUUID();
    @Getter @Setter(AccessLevel.PROTECTED)
    private Scene scene = null;
    private final AddRemoveQueue<Component> componentQueue = new AddRemoveQueue<>();

    @Getter @Setter
    private String name = "GameObject";
    @Getter
    private Transform transform;
    private final List<Component> components = new ArrayList<>();

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

    public static void fillComponents(@NonNull GameObject gameObject, @NonNull Iterable<Component> components) {
        gameObject.components.clear();

        Transform transform = null;
        for (var component : components) {
            if (Transform.class.isAssignableFrom(component.getClass()))
                transform = (Transform) component;

            gameObject.addComponent(component);
        }

        if (transform == null)
            transform = new Transform();

        gameObject.addComponent(transform);
        gameObject.transform = transform;
    }

    public Iterable<GameObject> getChildren() {
        return this.scene.getHierarchy().getChildren(this);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> clazz) {
        for (var component : this.components) {
            if (clazz.isAssignableFrom(component.getClass()))
                return (T) component;
        }

        return null;
    }

    public Iterable<Component> getComponents() {
        return this.components;
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
        this.removeQueuedComponents(false);
        this.addQueuedComponents(false);

        for (var component : this.components)
            component.onUpdate(delta);
    }

    @Override
    public void editorUpdate(double delta) {
        this.removeQueuedComponents(true);
        this.addQueuedComponents(true);
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

    public void addChild(GameObject child) {
        if (this.scene != child.getScene())
            return;

        this.scene.getHierarchy().addChild(this, child);
    }

    public void addComponent(@NonNull Component component) {
        if (component.getGameObject() != null)
            return;

        if (this.scene == null)
            this.processAddComponent(component);
        else
            this.componentQueue.add(component);
    }

    public void removeComponent(@NonNull Component component) {
        if (component.getGameObject() != this)
            return;

        if (this.scene == null)
            this.processRemoveComponent(component);
        else if (this.components.contains(component))
            this.componentQueue.remove(component);
    }

    private void addQueuedComponents(boolean isEditor) {
        Component component;
        while ((component = this.componentQueue.pollAdded()) != null) {
            if (this.processAddComponent(component) && !isEditor) {

                component.onAwake();
                component.onStart();

                if (this.scene.getMainCamera() == null && Camera.class.isAssignableFrom(component.getClass()))
                    this.scene.setMainCamera((Camera) component);
            }
        }
    }

    private void removeQueuedComponents(boolean isEditor) {
        Component component;
        while ((component = this.componentQueue.pollRemoved()) != null) {
            if (!isEditor) {
                if (component == this.scene.getMainCamera())
                    this.scene.setMainCamera(null);
                component.onDestroy();
            }

            this.processRemoveComponent(component);
        }
    }

    private boolean processAddComponent(Component component) {
        for (var c : this.components) {
            if (ReflectionUtil.shareAncestor(component.getClass(), c.getClass(), Component.class))
                return false;
        }

        this.components.add(component);
        component.setGameObject(this);

        return true;
    }

    private boolean processRemoveComponent(Component component) {
        // Cannot remove base transform component
        if (Transform.class.isAssignableFrom(component.getClass()))
            return false;

        this.components.remove(component);
        component.setGameObject(null);

        return true;
    }

    public GameObject getParent() {
        if (this.scene == null)
            return null;
        return this.scene.getHierarchy().getParent(this);
    }

    @Override
    public int hashCode() {
        return this.uuid.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof GameObject g && this.uuid.equals(g.uuid);
    }
}
