package io.github.lionarius.engine.renderer.buffer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.lwjgl.opengl.GL46;

import java.util.ArrayList;
import java.util.List;

@Getter
public class VertexBufferLayout {
	private final List<LayoutElement> elements = new ArrayList<>();
	private int stride = 0;

	public <T> void push(Class<T> clazz, int count, boolean normalized) {
		var type = 0;
		var size = 0;
		if (clazz == Boolean.class) {
			type = GL46.GL_BOOL;
			size += 1;
		} else if (clazz == Byte.class) {
			type = GL46.GL_UNSIGNED_BYTE;
			size = Byte.BYTES;
		} else if (clazz == Integer.class) {
			type = GL46.GL_UNSIGNED_INT;
			size = Integer.BYTES;
		} else if (clazz == Float.class) {
			type = GL46.GL_FLOAT;
			size = Float.BYTES;
		} else
			throw new IllegalArgumentException("Type is not supported");

		this.elements.add(new LayoutElement(type, count, size, normalized));
		this.stride += count * size;
	}

	@Getter
	@AllArgsConstructor
	protected static class LayoutElement {
		private int type;
		private int count;
		private int size;
		private boolean normalized;
	}
}
