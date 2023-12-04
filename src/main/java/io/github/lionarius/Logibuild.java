package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.Renderer;
import io.github.lionarius.engine.renderer.buffer.*;
import io.github.lionarius.engine.renderer.shader.Shader;
import io.github.lionarius.engine.renderer.shader.ShaderLoader;
import io.github.lionarius.engine.renderer.texture.Texture;
import io.github.lionarius.engine.renderer.texture.TextureLoader;
import io.github.lionarius.engine.util.BufferUtil;
import io.github.lionarius.engine.util.ProjectionUtil;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Cleanup;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Vector2f;
import org.lwjgl.opengl.GL46;

public final class Logibuild {
	private static final Logger LOGGER = LogManager.getLogger("Logibuild");
	@Getter
	private static Logibuild instance;

	private final Window window = new Window(1280, 720, "Logibuild");

	@Getter
	private final ResourceManager resourceManager = new ResourceManager("assets");
	@Getter
	private final InputHandler inputHandler = new InputHandler(this.window);
	@Getter
	private final KeybindHandler keybindHandler = new KeybindHandler(this.inputHandler);
	@Getter
	private final Renderer renderer = new Renderer(this.resourceManager);

	public Logibuild(String[] args) {
		if (instance != null)
			throw new IllegalStateException("Cannot create more than one game instance");
		instance = this;

		this.window.init();
		this.inputHandler.init();

		this.resourceManager.register(Shader.class, new ShaderLoader());
		this.resourceManager.register(Texture.class, new TextureLoader());

		this.renderer.init();
	}

	public void run() {
		Vector2f[] positions = {
				new Vector2f(0, 0),
				new Vector2f(500, 0),
				new Vector2f(500, 500),
				new Vector2f(0, 500)
		};
		var buffer = BufferUtil.vectorArrayToBuffer(positions);
		@Cleanup var vbo = new VertexBuffer(buffer);

		int[] indices = {
				0, 1, 2,
				2, 3, 0
		};
		@Cleanup var ibo = new IndexBuffer(indices);

		var layout = new VertexBufferLayout();
		layout.push(Float.class, 2, false);

		@Cleanup var vao = new VertexArray();
		vao.addBuffer(vbo, layout);

		var shader = this.resourceManager.get(Shader.class, "shader/rectangle.shader");

		double prevTime;
		double currentTime = TimeUtil.getApplicationTime();
		double dt = -1.0;

		vbo.unbind();
		ibo.unbind();
		vao.unbind();

		while (!this.window.shouldClose()) {
			this.inputHandler.update();
			this.keybindHandler.update();

			if (dt >= 0) {
				GL46.glClear(GL46.GL_COLOR_BUFFER_BIT);

				vao.bind();
				ibo.bind();
				shader.bind();
				shader.setUniform("u_View", ProjectionUtil.getOrthoProjectionCentered(0, 0, this.window.getWidth(), this.window.getHeight()));

				GL46.glDrawElements(GL46.GL_TRIANGLES, ibo.getCount(), GL46.GL_UNSIGNED_INT, 0);

				LOGGER.info("FPS {}", 1 / dt);
			}

			this.window.update();

			prevTime = currentTime;
			currentTime = TimeUtil.getApplicationTime();
			dt = currentTime - prevTime;
		}

		this.window.close();
	}

	private void update() {

	}

	private void render() {

	}
}
