package io.github.lionarius.engine.scene.object;

import io.github.lionarius.engine.scene.Renderable;
import io.github.lionarius.engine.scene.Scene;
import io.github.lionarius.engine.scene.Updatable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public final class GameObject implements Updatable, Renderable {
	private final List<Component> components = new ArrayList<>();
	@Getter @Setter
	private Scene scene;

	@SuppressWarnings("unchecked")
	public <T extends Component> T getComponent(Class<T> clazz) {
		for (var component : this.components) {
			if (clazz.isAssignableFrom(component.getClass()))
				return (T) component;
		}

		return null;
	}

	public void create() {

	}

	@Override
	public void render(double delta) {

	}

	@Override
	public void update(double delta) {

	}

	public void destroy() {

	}
}
