package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Camera;
import lombok.Getter;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Scene implements Updatable, Renderable {
    private final List<GameObject> gameObjects = new ArrayList<>();
    private final Queue<GameObject> addedObjects = new ArrayDeque<>();
    private final Queue<GameObject> removedObjects = new ArrayDeque<>();
    @Getter
    private boolean isEntered = false;
    @Getter
    private Camera mainCamera = null;

    public void addGameObject(GameObject gameObject) {
        if (this.isEntered)
            this.addedObjects.add(gameObject);
        else
            this.processAddGameObject(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        if (this.isEntered)
            this.removedObjects.add(gameObject);
        else
            this.processAddGameObject(gameObject);
    }

    public <T extends Component> T findFirst(Class<T> clazz) {
        for (var gameObject : this.gameObjects) {
            var component = gameObject.getComponent(clazz);
            if (component != null)
                return component;
        }

        return null;
    }

    public <T extends Component> List<T> findAll(Class<T> clazz) {
        var components = new ArrayList<T>();
        for (var gameObject : this.gameObjects) {
            var component = gameObject.getComponent(clazz);
            if (component != null)
                components.add(component);
        }

        return components;
    }

    public void enter() {
        for (var gameObject : this.gameObjects)
            gameObject.awake();

        this.mainCamera = this.findFirst(Camera.class);

        for (var gameObject : this.gameObjects)
            gameObject.start();

        this.isEntered = true;
    }

    public void leave() {
        for (var gameObject : this.gameObjects)
            gameObject.destroy();
    }

    @Override
    public final void update(double delta) {
        this.removeQueuedObjects();
        this.addQueuedObjects();

        for (var gameObject : this.gameObjects)
            gameObject.update(delta);
    }

    @Override
    public final void render(double delta) {
        for (var gameObject : this.gameObjects)
            gameObject.render(delta);
    }

    private void processAddGameObject(GameObject gameObject) {
        this.gameObjects.add(gameObject);
        gameObject.setScene(this);
    }

    private void processRemoveGameObject(GameObject gameObject) {
        this.gameObjects.remove(gameObject);
        gameObject.setScene(null);
    }

    private void addQueuedObjects() {
        if (!this.addedObjects.isEmpty()) {
            GameObject gameObject;
            while ((gameObject = this.addedObjects.poll()) != null) {
                this.processAddGameObject(gameObject);

                gameObject.awake();
                gameObject.start();
            }
        }
    }

    private void removeQueuedObjects() {
        if (!this.removedObjects.isEmpty()) {
            GameObject gameObject;
            while ((gameObject = this.removedObjects.poll()) != null) {
                gameObject.destroy();

                this.processRemoveGameObject(gameObject);
            }
        }
    }
}
