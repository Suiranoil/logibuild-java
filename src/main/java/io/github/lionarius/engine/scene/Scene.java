package io.github.lionarius.engine.scene;

import imgui.ImGui;
import imgui.flag.ImGuiTreeNodeFlags;
import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.util.AddRemoveQueue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Scene implements Updatable, Renderable {
    @Getter
    private final Hierarchy<GameObject> hierarchy = new Hierarchy<>();
    private transient final AddRemoveQueue<GameObject> objectsQueue = new AddRemoveQueue<>();
    @Getter
    private transient boolean isEntered = false;
    @Getter
    private transient Camera mainCamera = null;
    @Getter transient GameObject selectedGameObject = null;

    public void imguiHierarchy() {
        this.imguiGameObjectHierarchy(null);
    }

    private void imguiGameObjectHierarchy(GameObject gameObject) {
        var children = this.hierarchy.getChildren(gameObject);
        var isLeaf = children.isEmpty();

        var id = "root";
        var label = "Root";
        if (gameObject != null) {
            id = gameObject.toString();
            label = gameObject.getName();
        }

        var flags = ImGuiTreeNodeFlags.OpenOnArrow | ImGuiTreeNodeFlags.OpenOnDoubleClick | ImGuiTreeNodeFlags.SpanAvailWidth;
        var selected = this.selectedGameObject != null && this.selectedGameObject == gameObject;
        if (selected)
            flags |= ImGuiTreeNodeFlags.Selected;
        if (isLeaf)
            flags |= ImGuiTreeNodeFlags.Leaf | ImGuiTreeNodeFlags.NoTreePushOnOpen;

        ImGui.pushID(id);

        var open = ImGui.treeNodeEx("item", flags, label) && !isLeaf;

        if (ImGui.isItemClicked() && !ImGui.isItemToggledOpen())
            this.selectedGameObject = gameObject;

        if (gameObject != null) {
            if (ImGui.beginDragDropSource()) {
                ImGui.setDragDropPayload("gameObject", gameObject);
                ImGui.text(gameObject.getName());

                ImGui.endDragDropSource();
            }
        }

        if (ImGui.beginDragDropTarget()) {
            var payload = (GameObject) ImGui.acceptDragDropPayload("gameObject");
            if (payload != null)
                this.hierarchy.setParent(payload, gameObject);

            ImGui.endDragDropTarget();
        }

        if (open) {
            for (var element : children) {
                this.imguiGameObjectHierarchy(element);
            }
            ImGui.treePop();
        }

        ImGui.popID();
    }

    public void enter() {
        for (var gameObject : this.hierarchy)
            gameObject.awake();

        this.mainCamera = this.findFirst(Camera.class);

        for (var gameObject : this.hierarchy)
            gameObject.start();

        this.isEntered = true;
    }

    @Override
    public final void update(double delta) {
        this.removeQueuedObjects();
        this.addQueuedObjects();
        this.hierarchy.processChanges();

        for (var gameObject : this.hierarchy)
            gameObject.update(delta);
    }

    @Override
    public final void render(double delta) {
        for (var gameObject : this.hierarchy)
            gameObject.render(delta);
    }

    public void leave() {
        for (var gameObject : this.hierarchy)
            gameObject.destroy();
    }

    public void addGameObject(GameObject gameObject) {
        if (gameObject.getScene() != null)
            return;

        if (this.isEntered)
            this.objectsQueue.add(gameObject);
        else
            this.processAddGameObject(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        if (gameObject.getScene() != this)
            return;

        if (this.isEntered)
            this.objectsQueue.remove(gameObject);
        else
            this.processAddGameObject(gameObject);
    }

    public <T extends Component> T findFirst(Class<T> clazz) {
        for (var gameObject : this.hierarchy) {
            var component = gameObject.getComponent(clazz);
            if (component != null)
                return component;
        }

        return null;
    }

    public <T extends Component> List<T> findAll(Class<T> clazz) {
        var components = new ArrayList<T>();
        for (var gameObject : this.hierarchy) {
            var component = gameObject.getComponent(clazz);
            if (component != null)
                components.add(component);
        }

        return components;
    }

    protected void verifyIntegrity() {
        for (var gameObject : this.hierarchy)
            this.verifyGameObjectIntegrity(gameObject);
    }

    private void verifyGameObjectIntegrity(GameObject gameObject) {
        if (gameObject.getScene() != this)
            throw new IllegalStateException("There are game objects that do not belong to this scene");

        for (var child : this.hierarchy.getChildren(gameObject))
            this.verifyGameObjectIntegrity(child);
    }

    private void addQueuedObjects() {
        GameObject gameObject;
        while ((gameObject = this.objectsQueue.pollAdded()) != null) {
            this.processAddGameObject(gameObject);

            gameObject.awake();
            gameObject.start();
        }
    }

    private void removeQueuedObjects() {
        GameObject gameObject;
        while ((gameObject = this.objectsQueue.pollRemoved()) != null) {
            gameObject.destroy();

            this.processRemoveGameObject(gameObject);
        }
    }

    private void processAddGameObject(GameObject gameObject) {
        this.hierarchy.add(gameObject);
        gameObject.setScene(this);
    }

    private void processRemoveGameObject(GameObject gameObject) {
        this.hierarchy.remove(gameObject);
        gameObject.setScene(null);
    }
}
