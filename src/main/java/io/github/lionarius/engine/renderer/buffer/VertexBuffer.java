package io.github.lionarius.engine.renderer.buffer;

import io.github.lionarius.engine.renderer.OpenGLObject;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

public class VertexBuffer extends OpenGLObject {

	public VertexBuffer(BufferUsage usage, int size) {
		this.id = GL46.glCreateBuffers();
		GL46.glNamedBufferData(this.id, size, usage.getInner());
	}

	public VertexBuffer(ByteBuffer data) {
		this(BufferUsage.STATIC_DRAW, data);
	}

	public VertexBuffer(BufferUsage usage, float[] data) {
		this.id = GL46.glCreateBuffers();
		GL46.glNamedBufferData(this.id, data, usage.getInner());
	}

	public VertexBuffer(BufferUsage usage, ByteBuffer data) {
		this.id = GL46.glCreateBuffers();
		data.flip();
		GL46.glNamedBufferData(this.id, data, usage.getInner());
	}

	public void uploadData(float[] data) {
		GL46.glNamedBufferSubData(this.id, 0, data);
	}

	public void uploadData(ByteBuffer data) {
		data.flip();
		GL46.glNamedBufferSubData(this.id, 0, data);
	}

	@Override
	public void bind() {
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, this.id);
	}

	@Override
	public void unbind() {
		GL46.glBindBuffer(GL46.GL_ARRAY_BUFFER, 0);
	}

	@Override
	public void close() {
		GL46.glDeleteBuffers(this.id);
	}
}
