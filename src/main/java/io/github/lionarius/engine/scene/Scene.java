package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;
import io.github.lionarius.engine.collision.Collision2DUtils;
import io.github.lionarius.engine.resource.Resource;
import io.github.lionarius.engine.scene.builtin.Camera;
import io.github.lionarius.engine.scene.builtin.collision.Collider;
import io.github.lionarius.engine.scene.builtin.collision.Polygon2DCollider;
import io.github.lionarius.engine.util.AddRemoveQueue;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import org.javatuples.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Scene implements Updatable, Renderable, Resource {
    @Getter @Setter
    private String resourceName;
    @Getter
    private final Hierarchy<GameObject> hierarchy = new Hierarchy<>();
    private final transient AddRemoveQueue<GameObject> objectsQueue = new AddRemoveQueue<>();
    @Getter
    private transient boolean isPlaying = false;
    @Getter @Setter(AccessLevel.PROTECTED)
    private transient Camera mainCamera = null;
    @Getter @Setter
    private transient GameObject selectedGameObject = null;

    public GameObject findByUUID(UUID uuid) {
        for (var object : this.hierarchy) {
            if (object.getUuid().equals(uuid))
                return object;
        }

        return null;
    }

    public void enter() {
        for (var gameObject : this.hierarchy)
            gameObject.awake();

        this.mainCamera = this.findFirst(Camera.class);

        for (var gameObject : this.hierarchy)
            gameObject.start();

        this.isPlaying = true;
    }

    @Override
    public final void update(double delta) {
        this.removeQueuedObjects();
        this.addQueuedObjects();
        this.hierarchy.processChanges();

        for (var gameObject : this.hierarchy)
            gameObject.update(delta);

        // TODO: Collisions should not be handled by scene
        var colliders = this.findAll(Polygon2DCollider.class);
        var collisions = this.processCollisions2D(colliders);
        for (var collision : collisions) {
            var a = collision.getValue0();
            var b = collision.getValue1();
            a.getGameObject().collide(b);
        }
    }

    private Iterable<Pair<Collider, Collider>> processCollisions2D(Iterable<Polygon2DCollider> colliders) {
        var collisions = new ArrayList<Pair<Collider, Collider>>();

        for (var a : colliders) {
            for (var b : colliders) {
                if (a == b)
                    continue;

                if (a.getPolygon2D() == null || b.getPolygon2D() == null)
                    continue;

                if (a.getIgnoreGroup().equals(b.getCollisionGroup()))
                    continue;

                if (Collision2DUtils.intersect(a.getPolygon2D(), b.getPolygon2D()))
                    collisions.add(Pair.with(a, b));
            }
        }

        return collisions;
    }

    @Override
    public void editorUpdate(double delta) {
        this.removeQueuedObjects();
        this.addQueuedObjects();
        this.hierarchy.processChanges();

        for (var gameObject : this.hierarchy)
            gameObject.editorUpdate(delta);
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

        if (this.isPlaying)
            this.objectsQueue.add(gameObject);

        this.processAddGameObject(gameObject);

        this.hierarchy.add(gameObject);
    }

    public void removeGameObject(GameObject gameObject) {
        if (gameObject.getScene() != this)
            return;

        if (this.isPlaying)
            this.objectsQueue.remove(gameObject);
        else
            this.processRemoveGameObject(gameObject);

        if (this.selectedGameObject != null) {
            if (this.hierarchy.isInHierarchy(gameObject, this.selectedGameObject))
                this.selectedGameObject = null;
        }
        this.hierarchy.remove(gameObject);
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
        gameObject.setScene(this);
    }

    private void processRemoveGameObject(GameObject gameObject) {
        gameObject.setScene(null);
    }

    @Override
    public void close() {
    }
}
