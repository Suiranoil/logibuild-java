package io.github.lionarius.engine.renderer.buffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import org.lwjgl.opengl.GL46;

public class VertexArray extends OpenGLObject {
	public VertexArray() {
		this.id = GL46.glCreateVertexArrays();
	}

	@Override
	public void bind() {
		GL46.glBindVertexArray(this.id);
	}

	@Override
	public void unbind() {
		GL46.glBindVertexArray(0);
	}

	@Override
	public void close() {
		GL46.glDeleteVertexArrays(this.id);
	}

	public void addBuffer(VertexBuffer buffer, VertexBufferLayout layout) {
		this.bind();
		buffer.bind();

		var elements = layout.getElements();
		var offset = 0;
		for (int i = 0; i < elements.size(); i++) {
			var element = elements.get(i);
			GL46.glEnableVertexAttribArray(i);
			GL46.glVertexAttribPointer(i, element.getCount(), element.getType(), element.isNormalized(), layout.getStride(), offset);
			offset += element.getCount() * element.getSize();
		}
	}
}
