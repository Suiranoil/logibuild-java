package io.github.lionarius.engine.renderer.rectangle;

import io.github.lionarius.engine.renderer.buffer.VertexBufferLayout;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.GetToByteBuffer;
import lombok.AllArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.nio.ByteBuffer;

@AllArgsConstructor
public class Vertex implements GetToByteBuffer {
	private static final VertexBufferLayout LAYOUT;

	static {
		LAYOUT = new VertexBufferLayout();
		LAYOUT.push(Float.class, 3); // vec3 position
		LAYOUT.push(Float.class, 4); // vec4 color
		LAYOUT.push(Float.class, 2); // vec2 uv
		LAYOUT.push(Integer.class, 1); // int textureId
	}

	public static VertexBufferLayout getLayout() {
		return Vertex.LAYOUT;
	}


	private Matrix4f model;
	private Vector3f position;
	private Vector4f color;
	private Vector2f uv;
	private int textureId;

	public void setModel(Matrix4f model) {
		this.model.set(model);
	}

	public void setColor(Vector4f color) {
		this.color.set(color);
	}

	@Override
	public void get(ByteBuffer byteBuffer) {
		var pos = new Vector3f();
		this.position.mulPosition(this.model, pos).get(byteBuffer);
		BufferUtil.movePosition(byteBuffer, 3 * Float.BYTES);
		this.color.get(byteBuffer);
		BufferUtil.movePosition(byteBuffer, 4 * Float.BYTES);
		this.uv.get(byteBuffer);
		BufferUtil.movePosition(byteBuffer, 2 * Float.BYTES);
		byteBuffer.asIntBuffer().put(this.textureId);
		BufferUtil.movePosition(byteBuffer, Integer.BYTES);
	}
}
