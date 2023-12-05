package io.github.lionarius.engine.renderer;

import io.github.lionarius.engine.renderer.buffer.BufferUsage;
import io.github.lionarius.engine.renderer.buffer.IndexBuffer;
import io.github.lionarius.engine.renderer.buffer.VertexArray;
import io.github.lionarius.engine.renderer.buffer.VertexBuffer;
import io.github.lionarius.engine.renderer.rectangle.Vertex;
import io.github.lionarius.engine.renderer.shader.Shader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.Closeable;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.joml.Matrix4f;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL46;

import java.nio.ByteBuffer;

@RequiredArgsConstructor
public class Renderer implements Closeable {
	private static final int BATCH_SIZE = 1024;
	private static final int VERTEX_COUNT = 4 * BATCH_SIZE;
	private static final int INDICES_COUNT = 6 * BATCH_SIZE;


	@NonNull
	private final ResourceManager resourceManager;
	private int renderedQuads;
	private ByteBuffer vertexBuffer;
	private Vertex[] vertices;
	private Shader rectangleShader;
	private VertexBuffer vbo;
	private IndexBuffer ibo;
	private VertexArray vao;

	public void init() {
		this.rectangleShader = this.resourceManager.get(Shader.class, "shader/rectangle.shader");
		this.vertexBuffer = BufferUtils.createByteBuffer(Renderer.VERTEX_COUNT * Vertex.getLayout().getStride());
		this.vertices = new Vertex[] {
				new Vertex(new Matrix4f(), new Vector3f(0, 0, 0), new Vector4f(), new Vector2f(0, 0), -1),
				new Vertex(new Matrix4f(), new Vector3f(1, 0, 0), new Vector4f(), new Vector2f(1, 0), -1),
				new Vertex(new Matrix4f(), new Vector3f(1, 1, 0), new Vector4f(), new Vector2f(1, 1), -1),
				new Vertex(new Matrix4f(), new Vector3f(0, 1, 0), new Vector4f(), new Vector2f(0, 1), -1)
		};

		this.vbo = new VertexBuffer(BufferUsage.DYNAMIC_DRAW, Renderer.VERTEX_COUNT * Vertex.getLayout().getStride());

		var indices = new int[Renderer.INDICES_COUNT];
		var vertex = 0;
		for (int i = 0; i < Renderer.INDICES_COUNT; i += 6) {
			indices[i + 0] = 0 + vertex;
			indices[i + 1] = 1 + vertex;
			indices[i + 2] = 2 + vertex;
			indices[i + 3] = 2 + vertex;
			indices[i + 4] = 3 + vertex;
			indices[i + 5] = 0 + vertex;

			vertex += 4;
		}
		this.ibo = new IndexBuffer(indices);

		this.vao = new VertexArray();
		this.vao.addBuffer(this.vbo, Vertex.getLayout());
	}

	public void beginFrame() {
		this.renderedQuads = 0;
		this.ibo.setCount(0);
		this.vertexBuffer.position(0);
	}

	public void renderQuad(float x, float y, float z, float width, float height, Vector4f color) {
		if (this.renderedQuads >= Renderer.BATCH_SIZE) {
			return;
		}

		var model = new Matrix4f().translate(x, y, z).scale(width, height, 0);

		for (int i = 0; i < 4; i++) {
			this.vertices[i].setModel(model);
			this.vertices[i].setColor(color);
		}
		BufferUtil.objectArrayToBuffer(this.vertices, this.vertexBuffer);
		this.renderedQuads += 1;
	}

	public void endFrame(Matrix4f projection, Matrix4f view) {
		this.ibo.setCount(this.renderedQuads * 6);

		this.ibo.bind();
		this.vao.bind();
		this.rectangleShader.bind();
		this.vbo.uploadData(this.vertexBuffer);

		this.rectangleShader.setUniform("u_Projection", projection);
		this.rectangleShader.setUniform("u_View", view);

		GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);
		GL46.glDrawElements(GL46.GL_TRIANGLES, this.ibo.getCount(), GL46.GL_UNSIGNED_INT, 0);
	}

	@Override
	public void close() {
		this.vao.close();
		this.ibo.close();
		this.vbo.close();
	}
}
