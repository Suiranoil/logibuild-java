package io.github.lionarius.engine.scene;

import imgui.ImGui;
import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.editor.ImGuiUtil;
import io.github.lionarius.engine.scene.builtin.Transform;
import io.github.lionarius.engine.util.AddRemoveQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class GameObject implements Updatable, Renderable {
    @Getter @Setter
    private String name = "GameObject";
    private final List<Component> components = new ArrayList<>();
    @Getter @Setter(AccessLevel.PROTECTED)
    private transient Scene scene = null;
    @Getter
    private transient final Transform transform;
    private transient final AddRemoveQueue<Component> componentQueue = new AddRemoveQueue<>();

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

    public void addChild(GameObject child) {
        if (this.scene != child.getScene())
            return;

        this.scene.getHierarchy().addChild(this, child);
    }

    public void addComponent(@NonNull Component component) {
        if (component.getGameObject() != null)
            return;

        if (this.scene == null || !this.scene.isEntered())
            this.processAddComponent(component);
        else
            this.componentQueue.add(component);
    }

    public void removeComponent(@NonNull Component component) {
        if (component.getGameObject() != this)
            return;

        if (this.scene == null || !this.scene.isEntered())
            this.processRemoveComponent(component);
        else
            this.componentQueue.remove(component);
    }

    private void addQueuedComponents() {
        Component component;
        while ((component = this.componentQueue.pollAdded()) != null) {
            this.processAddComponent(component);

            component.onAwake();
            component.onStart();
        }
    }

    private void removeQueuedComponents() {
        Component component;
        while ((component = this.componentQueue.pollRemoved()) != null) {
            component.onDestroy();

            this.processRemoveComponent(component);
        }
    }

    private void processAddComponent(Component component) {
        for (var c : this.components) {
            if (c.getClass().isNestmateOf(component.getClass()))
                return;
        }
//        // Cannot add another transform component
//        if (Transform.class.isAssignableFrom(component.getClass()) && !this.components.isEmpty())
//            return;

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

    public GameObject getParent() {
        return this.scene.getHierarchy().getParent(this);
    }

    public void imgui() {
        var newName = ImGuiUtil.inputText("Name", this.name);
        if (!newName.isEmpty())
            this.name = newName;

        for (var component : this.components) {
            ImGui.separator();
            component.imgui();
        }
    }
}
