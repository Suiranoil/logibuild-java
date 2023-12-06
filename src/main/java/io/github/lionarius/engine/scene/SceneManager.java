package io.github.lionarius.engine.scene;

import io.github.lionarius.engine.Renderable;
import io.github.lionarius.engine.Updatable;

public class SceneManager implements Updatable, Renderable {
	private Scene currentScene = null;
	private Scene queuedScene = null;

    public void transitionTo(Scene newScene) {
		this.queuedScene = newScene;
	}

	@Override
	public void update(double delta) {
		if (this.queuedScene != null) {
			if (this.currentScene != null)
				this.currentScene.leave();

			this.currentScene = this.queuedScene;
			this.queuedScene = null;

			if (this.currentScene != null)
				this.currentScene.enter();
		}

		if (this.currentScene != null)
			this.currentScene.update(delta);
	}

	@Override
	public void render(double delta) {
		if (this.currentScene != null)
			this.currentScene.render(delta);
	}
}
