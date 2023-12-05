package io.github.lionarius;

import io.github.lionarius.engine.InputHandler;
import io.github.lionarius.engine.Window;
import io.github.lionarius.engine.keybind.KeybindHandler;
import io.github.lionarius.engine.renderer.Renderer;
import io.github.lionarius.engine.renderer.shader.Shader;
import io.github.lionarius.engine.renderer.shader.ShaderLoader;
import io.github.lionarius.engine.renderer.texture.Texture;
import io.github.lionarius.engine.renderer.texture.TextureLoader;
import io.github.lionarius.engine.resource.ResourceManager;
import io.github.lionarius.engine.util.ProjectionUtil;
import io.github.lionarius.engine.util.TimeUtil;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.joml.Matrix4f;
import org.joml.Vector4f;

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
		this.window.setVSync(false);
		this.inputHandler.init();

		this.resourceManager.register(Shader.class, new ShaderLoader());
		this.resourceManager.register(Texture.class, new TextureLoader());

		this.renderer.init();
	}

	public void run() {
		double prevTime;
		double currentTime = TimeUtil.getApplicationTime();
		double dt = -1.0;

		while (!this.window.shouldClose()) {
			this.inputHandler.update();
			this.keybindHandler.update();

			if (dt >= 0) {
				this.update(dt);

				this.render(dt);

				LOGGER.info("FPS {}", 1 / dt);
			}

			this.window.update();

			prevTime = currentTime;
			currentTime = TimeUtil.getApplicationTime();
			dt = currentTime - prevTime;
		}

		this.renderer.close();
		this.window.close();
	}

	private void update(double delta) {

	}

	private void render(double delta) {
		this.renderer.beginFrame();

		this.renderer.renderQuad((float) (0.5f / delta), 20, 0, 10, 250, new Vector4f(1, 0, 1, 1));

		this.renderer.endFrame(ProjectionUtil.getOrthoProjection(0, 0, this.window.getWidth(), this.window.getHeight()), new Matrix4f());
	}
}
