package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.util.Closeable;
import lombok.Getter;

@Getter
public abstract class OpenGLObject implements Closeable {
	protected int id;

	public abstract void bind();
	public abstract void unbind();
}
