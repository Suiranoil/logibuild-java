package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.scene.object.Component;
import io.github.lionarius.engine.scene.object.GameObject;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Scene implements Updatable, Renderable {
	private final List<GameObject> gameObjects = new ArrayList<>();
	private final Queue<GameObject> addedObjects = new ArrayDeque<>();
	private final Queue<GameObject> removedObjects = new ArrayDeque<>();
	private boolean isSceneEntered = false;

	public void addGameObject(GameObject gameObject) {
		gameObject.setScene(this);

		if (this.isSceneEntered)
			this.addedObjects.add(gameObject);
		else
			this.gameObjects.add(gameObject);
	}

	public void removeGameObject(GameObject gameObject) {
		if (this.isSceneEntered)
			this.removedObjects.add(gameObject);
		else
			this.gameObjects.remove(gameObject);
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
		if (!this.gameObjects.isEmpty()) {
			for (var gameObject : this.gameObjects)
				gameObject.create();
		}

		this.isSceneEntered = true;
	}

	public void leave() {
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

	private void addQueuedObjects() {
		if (!this.addedObjects.isEmpty()) {
			GameObject gameObject;
			while ((gameObject = this.addedObjects.poll()) != null) {
				this.gameObjects.add(gameObject);
				gameObject.create();
			}
		}
	}

	private void removeQueuedObjects() {
		if (!this.removedObjects.isEmpty()) {
			GameObject gameObject;
			while ((gameObject = this.removedObjects.poll()) != null) {
				gameObject.destroy();
				this.gameObjects.remove(gameObject);
			}
		}
	}
}
