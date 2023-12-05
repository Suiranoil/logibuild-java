package io.github.lionarius.engine.renderer.buffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import lombok.Getter;
import lombok.Setter;
import org.lwjgl.opengl.GL46;

@Setter
@Getter
public class IndexBuffer extends OpenGLObject {
	private int count;

	public IndexBuffer(BufferUsage usage, int size) {
		this.id = GL46.glCreateBuffers();
		this.count = 0;
		GL46.glNamedBufferData(this.id, (long) size * Integer.BYTES, usage.getInner());
	}

	public IndexBuffer(int[] data) {
		this(BufferUsage.STATIC_DRAW, data);
	}

	public IndexBuffer(BufferUsage usage, int[] data) {
		this.id = GL46.glCreateBuffers();
		this.count = data.length;
		GL46.glNamedBufferData(this.id, data, usage.getInner());
	}

	public void uploadData(int[] data) {
		this.count = data.length;
		GL46.glNamedBufferSubData(this.id, 0, data);
	}

	@Override
	public void bind() {
		GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, this.id);
	}

	@Override
	public void unbind() {
		GL46.glBindBuffer(GL46.GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void close() {
		GL46.glDeleteBuffers(this.id);
	}
}
