package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.util.AddRemoveQueue;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

public class Scene implements Updatable, Renderable {
    private final List<GameObject> gameObjects = new ArrayList<>();
    private final AddRemoveQueue<GameObject> objectsQueue = new AddRemoveQueue<>();
    @Getter
    private boolean isEntered = false;
    @Getter
    private Camera mainCamera = null;

    public void enter() {
        for (var gameObject : this.gameObjects)
            gameObject.awake();

        this.mainCamera = this.findFirst(Camera.class);

        for (var gameObject : this.gameObjects)
            gameObject.start();

        this.isEntered = true;
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

    public void leave() {
        for (var gameObject : this.gameObjects)
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

    protected void verifyIntegrity() {
        for (var gameObject : this.gameObjects)
            this.verifyGameObjectIntegrity(gameObject);
    }

    private void verifyGameObjectIntegrity(GameObject gameObject) {
        if (gameObject.getScene() != this)
            throw new IllegalStateException("There are game objects that do not belong to this scene");

        for (var child : gameObject.getChildren())
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
        this.gameObjects.add(gameObject);
        gameObject.setScene(this);
    }

    private void processRemoveGameObject(GameObject gameObject) {
        this.gameObjects.remove(gameObject);
        gameObject.setScene(null);
    }
}
