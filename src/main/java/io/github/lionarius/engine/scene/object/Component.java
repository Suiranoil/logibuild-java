package io.github.lionarius.engine.scene.object;

import io.github.lionarius.engine.scene.Renderable;
import io.github.lionarius.engine.scene.Updatable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Component implements Updatable, Renderable {
	@NonNull
	private final GameObject gameObject;

	@Override
	public void update(double delta) {
	}

	@Override
	public void render(double delta) {
	}
}
